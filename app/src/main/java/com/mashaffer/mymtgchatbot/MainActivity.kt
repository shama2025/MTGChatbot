package com.mashaffer.mymtgchatbot

import Card
import Rulings
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.LruCache
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
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
    private val phraseBtn: Button by lazy { findViewById(R.id.phraseBtn) }

    // Extra Variables
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var util: Util
    private var chat = mutableListOf<ChatMessage>()

    // Companion Object
    companion object {
        private const val TAG = "MainActivity"
        private val cache = LruCache<String, String?>(2)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // View model for listening to saved data
        util = ViewModelProvider(this)[Util::class.java]

        // Initialize Main activity
        initMainActivity()

        // Observe the LiveData for card data
        util.cardData.observe(this, Observer { data ->
            if (data.card != null) {
                data.let {
                    it.card?.let { it1 -> handleCardData(it1, it.question, it.additionalInfo) }
                }
            } else {
                apiErrorPopUp()
            }

        })

        // Observe the LiveData for rule data
        util.cardRuleData.observe(this, Observer { data ->
            if (data?.rulings != null) {
                data.let {
                    it.rulings?.let { it1 -> handleCardRuleData(it1, it.userQuery) }
                }
            } else {
                apiErrorPopUp()
            }

        })

        // Observe the LiveData for set data
        util.cardSetData.observe(this, Observer { data ->
            if (data?.set != null) {
                data.let {
                    it.set?.let { it1 -> handleCardSetData(it1, it.userQuery) }
                }
            } else {
                apiErrorPopUp()
            }
        })
    }

    // Displays Error message when API fails
    private fun apiErrorPopUp() {
        Log.i(TAG, "Displayed API Error Popup")
        val builder = AlertDialog.Builder(this)
        val errorPopUp = layoutInflater.inflate(R.layout.api_error_alert_dialog, null)

        builder.setView(errorPopUp).setNegativeButton("Close") { dialog, _ ->
            dialog.dismiss()
        }.show()

    }

    // Initialize function
    private fun initMainActivity() {
        displayPhrasesKey()
        requestPermissions()
        onSpeechListenerSetup()
        micBtnSpeechListener()
        keyboardListener()
    }

    // Displays an alert dialog to help with phrases
    private fun displayPhrasesKey() {
        val builder = AlertDialog.Builder(this)

        phraseBtn.setOnClickListener {
            val display = layoutInflater.inflate(R.layout.phrases_key_alert_dialog_layout, null)

            builder.setView(display).setNegativeButton("Close") { dialog, _ ->
                    dialog.dismiss()
                }.show()
        }
    }


    // Listener for keyboard
    private fun keyboardListener() {
        userTextInput.setOnEditorActionListener { textView, i, keyEvent ->
            if (keyEvent != null && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == KeyEvent.ACTION_DOWN) {
                val userInput = userTextInput.text.toString()
                Log.d("KeyboardListener", "User entered: $userInput")
                sendData(userInput)
                userTextInput.text.clear()
                true
            } else {
                false
            }
        }
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
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
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

    // Sends Data to backend depending on question
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
                util.getCardData(cardName, question, false)
            }

            cardRulesQuestion.matchEntire(question) != null -> {
                val match = cardRulesQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                Log.i(TAG, "Result after regex card rules filter: $cardName")
                util.getCardData(cardName, question, true)
                cache.get("ruling")?.let { util.getCardRuleData(it, question) }
            }

            cardSetQuestion.matchEntire(question) != null -> {
                val match = cardSetQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                Log.i(TAG, "Result after regex card set filter: $cardName")
                util.getCardData(cardName, question, true)
                cache.get("set")?.let { util.getCardSetData(it, question) }
            }
        }
    }

    // Request microphone permission
    private fun requestPermissions() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    Toast.makeText(this, "Mic permission granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Mic permission denied", Toast.LENGTH_SHORT).show()
                    micBtn.isEnabled = false
                }
            }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Handle the card data response
    private fun handleCardData(data: Card, question: String?, flag: Boolean) {
        var output = ""

        cache.put("ruling", data.rulingsUri.substringAfter("/cards/").substringBefore("/rulings"))
        cache.put("set", data.setUri.substringAfter("/sets/"))

        if (!flag) {
            val manaColor = formatManaColor(data.colors)

            output = if (data.power == null && data.toughness == null || data.manaCost == " ") {
                "The card ${data.name} has no power or toughness. " + "It has no mana cost and is in the color identity of ${manaColor}. ${data.name} has the ability ${data.oracleText}."
            } else {
                "The card ${data.name} has a base power of ${data.power} and a base toughness of ${data.toughness}. " + "It has a mana cost of ${data.manaCost}and is in the color identity of ${manaColor}. ${data.name} has the ability ${data.oracleText}."
            }

            // Format string to handle more scryfall syntax
            output = output.replace("{T}", "Tap").replace("{U}", "Blue ").replace("{G}", "Green ")
                .replace("{B}", "Black ").replace("{R}", "Red ").replace("{W}", "White ")
                .replace("{C}", "Colorless ")

            Log.i(TAG, output)

            updateChat(output, question)
        }
    }

    // Updates the UI
    private fun updateChat(aiResponse: String, userQuery: String?) {
        // Add users question to list
        userQuery?.let { ChatMessage(Actor.USER, it) }?.let { chat.add(it) }

        // Add Ai response
        chat.add(ChatMessage(Actor.AI, aiResponse))

        Log.i(TAG, "Updated chat array: $chat")
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = UserAiChatAdapter(chat)
    }

    // Handle the card rule data response
    private fun handleCardRuleData(data: Rulings, question: String?) {
        Log.i(TAG, "Data from API for card rules data: $data")

        if (data.moreData.isEmpty()) {
            Log.i(TAG, "No rulings available")
            updateChat("No rulings available", question)
        }else{
            var output = "The rulings are: "
            data.moreData.forEach { ruling ->
                output += ruling.comment
            }
            Log.i(TAG, "Output for rules: $output")
            updateChat(output, question)
        }
    }

    // Handle the card set data response
    private fun handleCardSetData(data: CardSet, question: String?) {
        Log.i(TAG, "Data from API for card set data: $data")

        val output = "This card is from the set: ${data.name}."
        Log.i(TAG, output)
        updateChat(output, question)
    }

    // Format the mana color
    private fun formatManaColor(manaColor: List<String>?): String {
        if (manaColor.isNullOrEmpty()) {
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
        return output.replace("U", "Blue ").replace("G", "Green ").replace("B", "Black ")
            .replace("R", "Red ").replace("W", "White ").replace("C", "Colorless ")
    }
}
