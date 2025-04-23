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
    private var chat = mutableListOf<ChatMessage>()

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
                it.card?.let { it1 -> handleCardData(it1, it.question, it.additionalInfo) }
            }
        })

        // Observe the LiveData for rule data
        util.cardRuleData.observe(this, Observer { data ->
            data?.let {
                it.rulings?.let { it1 -> handleCardRuleData(it1,it.userQuery) }
            }
        })

        // Observe the LiveData for set data
        util.cardSetData.observe(this, Observer { data ->
            data?.let {
                it.set?.let { it1 -> handleCardSetData(it1,it.userQuery) }
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
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault())

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0)
                    .toString()
                Log.i(TAG, "Data from listener: $data")
                sendData(data)
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
    }

    // Sends data to backend (util function)
    private fun sendData(question: String) {
        val genRuleQuestion = Regex("""card name\s+(.+)""", RegexOption.IGNORE_CASE)
        val cardRulesQuestion = Regex("""card rules\s+(.+)""", RegexOption.IGNORE_CASE)
        val cardSetQuestion = Regex("""card set\s+(.+)""", RegexOption.IGNORE_CASE)

        Log.i(TAG, "Question asked: $question")

        when {
            genRuleQuestion.matchEntire(question) != null -> {
                val match = genRuleQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName, question,false)
            }

            cardSetQuestion.matchEntire(question) != null -> {
                val match = cardSetQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName, question,true)
                cache.get("ruling")?.let { util.getCardSetData(it,question) }
            }

            cardRulesQuestion.matchEntire(question) != null -> {
                val match = cardRulesQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                Log.i(TAG, "Result after regex filter: $cardName")
                util.getCardData(cardName, question,true)
                cache.get("set")?.let { util.getCardSetData(it,question) }
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
    private fun handleCardData(data: Card, question:String?, flag: Boolean) {
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

            // Format string to handle more scryfall syntax
            output = output
                .replace("{U}", "Blue")
                .replace("{G}", "Green")
                .replace("{B}", "Black")
                .replace("{R}", "Red")
                .replace("{W}", "White")
                .replace("{C}", "Colorless")
                .replace("{T}", "Tap")

            Log.i(TAG, output)

            updateChat(output,question)
        }
    }

    private fun updateChat(aiResponse: String, userQuery: String?) {
        // Add users question to list
        userQuery?.let { ChatMessage(Actor.USER, it) }?.let { chat.add(it) }

        // Add Ai response
        chat.add(ChatMessage(Actor.AI,aiResponse))

        Log.i(TAG, "Updated chat array: $chat")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserAiChatAdapter(chat)
    }

    // Handle the card rule data response
    private fun handleCardRuleData(data: Rulings,question: String?) {
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
        updateChat(output, question)
    }

    // Handle the card set data response
    private fun handleCardSetData(data: CardSet,question: String?) {
        Log.i(TAG, "Data from API for card set data: $data")

        val output = "This card is from the set: ${data.name}."
        Log.i(TAG, output)
        updateChat(output, question)
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
