package com.mashaffer.mymtgchatbot.ui

import Rulings
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.LruCache
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mashaffer.mymtgchatbot.R
import com.mashaffer.mymtgchatbot.ui.UserAiChatAdapter
import com.mashaffer.mymtgchatbot.util.Util
import com.mashaffer.mymtgchatbot.chat.Actor
import com.mashaffer.mymtgchatbot.chat.ChatMessage
import com.mashaffer.mymtgchatbot.model.Card
import com.mashaffer.mymtgchatbot.model.CardResponse
import com.mashaffer.mymtgchatbot.model.CardSet
import com.mashaffer.mymtgchatbot.model.RulingResponse
import com.mashaffer.mymtgchatbot.model.SetResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    // UI variables
    private val micBtn: ImageButton by lazy { findViewById(R.id.micBtn) }
    private val userTextInput: EditText by lazy { findViewById(R.id.cardInput) }
    private val phraseBtn: ImageButton by lazy { findViewById(R.id.phraseBtn) }



    // Extra Variables
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var speechRecognizer: SpeechRecognizer? = null
    private lateinit var util: Util
    lateinit var recyclerView: RecyclerView
    lateinit var chatAdapter: UserAiChatAdapter

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
            when (data) {
                is CardResponse.CardData -> {
                    // Success case, pass the card, question, additionalInfo
                    handleCardData(data.card, data.question, data.additionalInfo)
                }

                is CardResponse.CardError -> {
                    // Error case, you can handle error message and question here
                    Log.e(TAG, "API Error: ${data.errorMsg}")
                    // Optionally, update chat with error or show error popup
                    updateChat(data.errorMsg, data.question)
                }

                null -> {
                    // Optionally handle null case if LiveData can emit null
                    Log.e(TAG, "cardData emitted null")
                }
            }
        })

// Observe the LiveData for rule data
        util.cardRuleData.observe(this, Observer { data ->
            when (data) {
                is RulingResponse.RulingData -> {
                    if (data.rulings != null) {
                        handleCardRuleData(data.rulings, data.userQuery)
                    } else {
                        //  apiErrorPopUp()
                    }
                }

                is RulingResponse.RulingError -> {
                    Log.e(TAG, "Rule API error: ${data.errorMsg}")
                    //  apiErrorPopUp()
                    updateChat(data.errorMsg, data.userQuery)
                }

                null -> {
                    // apiErrorPopUp()
                }
            }
        })

// Observe the LiveData for set data
        util.cardSetData.observe(this, Observer { data ->
            when (data) {
                is SetResponse.SetData -> {
                    if (data.set != null) {
                        handleCardSetData(data.set, data.userQuery)
                    } else {
                        //   apiErrorPopUp()
                    }
                }

                is SetResponse.SetError -> {
                    Log.e(TAG, "Set API error: ${data.errorMsg}")
                    //apiErrorPopUp()
                    updateChat(data.errorMsg, data.userQuery)
                }

                null -> {
                    // apiErrorPopUp()
                }
            }
        })

        recyclerView = findViewById<RecyclerView>(R.id.chatRoom)
        chatAdapter = UserAiChatAdapter()
        recyclerView.adapter = chatAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

// Used for debugging on the physical device post release
    // Displays Error message when API fails
//    fun apiErrorPopUp(question: String) {
//        Log.i(TAG, "Displayed API Error Popup")
//
//        val builder = AlertDialog.Builder(this)
//        val errorPopUp = layoutInflater.inflate(R.layout.api_error_alert_dialog, null)
//
//        val tmp = errorPopUp.findViewById<TextView>(R.id.errorPopUp)
//        tmp.text = question
//
//        builder.setView(errorPopUp)
//            .setNegativeButton("Close") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .show()
//    }

    // Initialize function
    private fun initMainActivity() {
        hideSystemBar(this)
        displayPhrasesKey()
        requestPermissions()
        onSpeechListenerSetup()
        micBtnSpeechListener()
        keyboardListener()
    }

    // Hides system bar on load
    private fun hideSystemBar(activity: MainActivity){
       val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
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
        userTextInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                val userInput = userTextInput.text.toString()
                Log.d(TAG, "User entered: $userInput")
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
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

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
                waitForCacheAndProceedRules(question)
            }

            cardSetQuestion.matchEntire(question) != null -> {
                val match = cardSetQuestion.find(question)
                val cardName = match?.groupValues?.get(1)?.trim()
                util.getCardData(cardName, question, true)
               waitForCacheAndProceedSet(question)
            }
        }
    }

    // Waits for ruling cache to be non-null
    fun waitForCacheAndProceedRules(question: String) {
        lifecycleScope.launch {
            val maxWaitTime = 5000L // max 5 seconds
            val startTime = System.currentTimeMillis()
            val delayInterval = 100L

            while (cache.get("ruling") == null && System.currentTimeMillis() - startTime < maxWaitTime) {
                // Show progress bar if needed
                delay(delayInterval)
            }

            val ruling = cache.get("ruling")
            if (ruling != null) {
                util.getCardRuleData(ruling, question)
            } else {
                // Timeout handling
                Log.e(TAG, "Timed out waiting for cache to be populated.")
            }
        }
    }

    // Waits for set in L1 cache to be non-null
    fun waitForCacheAndProceedSet(question: String) {
        lifecycleScope.launch {
            val maxWaitTime = 5000L // max 5 seconds
            val startTime = System.currentTimeMillis()
            val delayInterval = 100L

            while (cache.get("ruling") == null && System.currentTimeMillis() - startTime < maxWaitTime) {
                // Show progress bar if needed
                delay(delayInterval)
            }

            val ruling = cache.get("set")
            if (ruling != null) {
                util.getCardSetData(ruling, question)
            } else {
                // Timeout handling
                Log.e(TAG, "Timed out waiting for cache to be populated.")
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
    private fun handleCardData(data: Card?, question: String?, flag: Boolean) {
        var output = ""

        cache.put("ruling", data?.rulingsUri?.substringAfter("/cards/")?.substringBefore("/rulings"))
        cache.put("set", data?.setUri?.substringAfter("/sets/"))

        if (!flag) {
            val manaColor = formatManaColor(data?.colors)

            output = if (data?.power == null && data?.toughness == null || data.manaCost == " ") {
                "The card ${data?.name} has no power or toughness. " + "It has no mana cost and is in the color identity of ${manaColor}. ${data?.name} has the ability ${data?.oracleText}."
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

        // Updates the chat recylcer view
        fun updateChat(aiResponse: String, userQuery: String?) {
            userQuery?.let {
                val userMessage = ChatMessage(Actor.USER, it)
                chatAdapter.addMessage(userMessage)
            }

            val aiMessage = ChatMessage(Actor.AI, aiResponse)
            chatAdapter.addMessage(aiMessage)

            recyclerView.scrollToPosition(chatAdapter.itemCount - 1)
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