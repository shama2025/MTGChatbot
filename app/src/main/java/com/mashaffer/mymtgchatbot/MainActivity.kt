package com.mashaffer.mymtgchatbot

import Card
import Rulings
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.LruCache
import android.util.Log
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer

class MainActivity : ComponentActivity() {
    // UI variables
    private val micBtn: ImageButton by lazy { findViewById(R.id.micBtn) }
    private val userTextInput: EditText by lazy { findViewById(R.id.cardInput) }
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.chatRoom) }

    // Extra Variables
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var util: Util

    companion object {
        private const val TAG = "MainActivity"
        private val cache = LruCache<String, String?>(2)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        util = ViewModelProvider(this).get(Util::class.java)

        initMainActivity(this)

        // Observe the LiveData for card data
        util.cardData.observe(this, Observer { data ->
            data?.let {
                it.card?.let { it1 -> handleCardData(it1,it.additionalInfo) }
            }
        })

        // Observe the LiveData for rule data
        util.cardRuleData.observe(this, Observer { data ->
            data?.let {
                handleCardRuleData(it)
            }
        })

        // Observe the LiveData for set data
        util.cardSetData.observe(this, Observer { data ->
            data?.let {
                handleCardSetData(it)
            }
        })
    }

    private fun initMainActivity(context: Context) {
        requestPermissions()
        onSpeechListenerSetup()
        micBtnSpeechListener()
    }

    // Listener for microphone button
    @SuppressLint("ClickableViewAccessibility")
    private fun micBtnSpeechListener() {
        micBtn.setOnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    speechRecognizer?.startListening(intent)
                }
                MotionEvent.ACTION_UP -> {
                    speechRecognizer?.stopListening()
                    micBtn.performClick()
                }
            }
            true
        }
    }

    // Build a speech recognizer object
    private fun onSpeechListenerSetup() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0).toString()
                Log.i(TAG, "Data from listener: $data")
                sendData(data)
            }
            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    // Sends data to backend (util function)
    private fun sendData(question: String) {
        val genRuleQuestion = Regex("""card name \s+(?<card>.+)""",RegexOption.IGNORE_CASE)
        val cardRulesQuestion = Regex("""card rules \s+(?<card>.+)""",RegexOption.IGNORE_CASE)
        val cardSetQuestion = Regex("""card set \s+(?<card>.+)""",RegexOption.IGNORE_CASE)

        when{
            genRuleQuestion.matches(question)->{
                val match = genRuleQuestion.find(question)
                val cardName = match?.groups?.get("card")?.value?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName,false)
            }
            cardSetQuestion.matches(question)->{
                val match = genRuleQuestion.find(question)
                val cardName = match?.groups?.get("card")?.value?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName,true)
                // get rules id from cache
                cache.get("ruling")?.let { util.getCardSetData(it) }
            }
            cardRulesQuestion.matches(question)->{
                val match = genRuleQuestion.find(question)
                val cardName = match?.groups?.get("card")?.value?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName,true)
                // Get set id from cache
                cache.get("set")?.let {util.getCardSetData(it)}
            }
        }
    }

    // Request microphone permission
    private fun requestPermissions() {
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this, "Mic permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Mic permission denied", Toast.LENGTH_SHORT).show()
                micBtn.isEnabled = false
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle the card data response
    private fun handleCardData(data: Card, flag: Boolean) {
        var output = ""

        cache.put("ruling", data.rulingsUri)
        cache.put("set", data.setUri)

        if(!flag){
            val manaCost = formatManaCost(data.manaCost)
            val manaColor = formatManaColor(data.colors)

            output = if (data.power == null && data.toughness == null || manaCost == " ") {
                "The card ${data.name} has no power or toughness. " +
                        "It has no mana cost and is in the color identity of ${manaColor}. ${data.name} has the ability ${data.oracleText}."
            } else {
                "The card ${data.name} has a base power of ${data.power} and a base toughness of ${data.toughness}. " +
                        "It has a mana cost of ${manaCost} and is in the color identity of ${manaColor}. ${data.name} has the ability ${data.oracleText}."
            }

            Log.i(TAG, output)

            // Will need to make an object where 1 element has both user and ai chat
            // [["Ai...","User..."],["Ai...","User..."]]

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = AIChatAdapter(output)
        }
    }

    // Handle the card rule data response
    private fun handleCardRuleData(data: Rulings) {
        Log.i(TAG, "Data from API for card rules data: $data")

        if (data.moreData.isNullOrEmpty()) {
            Log.i(TAG, "No rulings available")
            return
        }

        var output = "The rulings are: "
        data.moreData.forEach { ruling ->
            output += ruling.comment
        }
        Log.i(TAG, output)
    }

    // Handle the card set data response
    private fun handleCardSetData(data: CardSet) {
        Log.i(TAG, "Data from API for card set data: $data")

        val output = "This card is from the set: ${data.name}."
        Log.i(TAG, output)
    }

    // Format the mana cost
    private fun formatManaCost(manaCost: String?): String {
        val manaCostFormat = mutableListOf<String>()
        val splitCost = manaCost.toString().split('{', '}')
        var output = ""

        splitCost.forEach { cost ->
            if (cost.isNotBlank()) {
                manaCostFormat.add(cost)
            }
        }

        manaCostFormat.forEachIndexed { index, cost ->
            val isLast = index == manaCostFormat.lastIndex
            if (isLast) {
                output += cost.uppercase()
            } else {
                output += "$cost, "
            }
        }
        return output
    }

    // Format the mana color
    private fun formatManaColor(manaColor: List<String>?): String {
        if (manaColor == null || manaColor.isEmpty()) {
            return "Colorless"
        }

        var output = ""
        manaColor.forEachIndexed { index, color ->
            val isLast = index == manaColor.lastIndex
            if (isLast) {
                output += color.uppercase()
            } else {
                output += "$color, "
            }
        }
        return output
    }
}
