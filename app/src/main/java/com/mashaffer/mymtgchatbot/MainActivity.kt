package com.mashaffer.mymtgchatbot

import Card
import Rulings
import android.Manifest
import android.R.layout
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.util.LruCache
import android.view.MotionEvent
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


class MainActivity : ComponentActivity(),ScryfallCallback {
    // UI variables
    private val micBtn: ImageButton by lazy {findViewById(R.id.micBtn)}
    private val userTextInput: EditText by lazy {findViewById(R.id.cardInput)}

    // Extra Variables
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private var speechRecognizer: SpeechRecognizer? = null

    companion object{
        private const val TAG = "MainActivity"
        private val util: Util = Util()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.main_activity)

        initMainActivity(this)
    }

    private fun initMainActivity(context: Context) {

        initImmersiveMode()

        val flag = checkSpeechRecognizer(context)
        if(!flag){
            Log.i(TAG, "Speech Recognition isn't supported")
            Toast.makeText(this,"Speech Input is not supported",Toast.LENGTH_LONG).show()
        }
        Log.i(TAG, "Supports Speech Recognition")

        requestPermissions()

        onKeyboardListen()

        onSpeechListenerSetup()

        micBtnSpeechListener()
    }

    private fun initImmersiveMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window,window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, windowInsets ->
            if (windowInsets.isVisible(WindowInsetsCompat.Type.navigationBars())
                || windowInsets.isVisible(WindowInsetsCompat.Type.statusBars())) {

                    windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

            }
            ViewCompat.onApplyWindowInsets(view, windowInsets)
        }
    }

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



    private fun onSpeechListenerSetup() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, java.util.Locale.getDefault());

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {
            }

            override fun onBeginningOfSpeech() {

            }

            override fun onRmsChanged(v: Float) {
            }

            override fun onBufferReceived(bytes: ByteArray) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(i: Int) {
            }

            override fun onResults(bundle: Bundle) {
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.get(0).toString()
                Log.i(TAG, "Data from listener:$data")
                sendData(data)
            }

            override fun onPartialResults(bundle: Bundle) {
            }

            override fun onEvent(i: Int, bundle: Bundle) {
            }
        })
    }

    private fun sendData(data: String) {
        val regex = Regex("""card name ([\w\s]+)""")
        val result = regex.find(data)

        if (result == null) {
            Log.i(TAG, "Regex match failed — no result")
            return
        }

        val cardName = result.groupValues[1].trim()

        Log.i(TAG, "Result after regex filter: $cardName")
        util.getCardData(MainActivity(), cardName)
    }



    private fun requestPermissions() {
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Mic permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Mic permission denied", Toast.LENGTH_SHORT).show()
                micBtn.isEnabled = false
            }
        }

        // Now actually launch the request
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            // Permission already granted
        }
    }

//        util.getCardRuleData(MainActivity(),"c0728027-a1ec-4814-87c4-10c3baced0e0")
//        util.getCardSetData(MainActivity(), "5a645837-b050-449f-ac90-1e7ccbf45031")



    private fun onKeyboardListen(){
        userTextInput.setOnKeyListener{v,keycode, event ->
            if(event.action == android.view.KeyEvent.ACTION_DOWN && keycode == android.view.KeyEvent.KEYCODE_ENTER){
                Log.i(TAG, "User pressed enter")
                // Pass the data to the recycler view so it
                // can append the data to the chat box

                // Then this will also call the util.GetCardGenData
                true
            }else{
                false
            }

        }
    }

    /**
     * This function checks if the given phone supports Speech Recognition
     */
    private fun checkSpeechRecognizer(context: Context): Boolean {
        return SpeechRecognizer.isRecognitionAvailable((context))
    }

    /**
     * This function will handle and format the general card data
     */
    override fun onGetCardData(data: Card?) {
        val cache = LruCache<String,String?>(2)
        cache.put("ruling", data?.rulingsUri)
        cache.put("set", data?.setUri)
        val manaCost = formatManaCost(data?.manaCost)
        val manaColor = formatManaColor(data?.colors)
        if(data?.power == null && data?.toughness == null && manaCost == " "){
            val output = "The card ${data?.name} has no power or toughness. " +
                    "It has no mana cost and is in the color identity of ${manaColor}." + " ${data?.name} has the ability ${data?.oracleText}."
        }
        val output = "The card ${data?.name} has a base power of ${data?.power} and a base toughness of ${data?.toughness}. " +
                "It has a mana cost of ${manaCost} and is in the color identity of ${manaColor}." + " ${data?.name} has the ability ${data?.oracleText}."
        Log.i(TAG, output)
            // Pass the output to the AI recycler view
    }

    /**
     * This function will format the mana cost to make it readable
     */
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
                when (cost.uppercase()) {
                    "R" -> output += " and Red"
                    "B" -> output += " and Black"
                    "U" -> output += " and Blue"
                    "W" -> output += " and White"
                    "G" -> output += " and Green"
                    else -> {
                        // If it's a number (colorless mana)
                        output += cost.toIntOrNull()?.let { " and $it colorless" }
                            ?: "Colorless/Generic/Any Colors"
                    }
                }
            } else {
                when (cost.uppercase()) {
                    "R" -> output += " Red,"
                    "B" -> output += " Black,"
                    "U" -> output += " Blue,"
                    "W" -> output += " White,"
                    "G" -> output += " Green,"
                    else -> {
                        // If it's a number (colorless mana)
                        output += cost.toIntOrNull()?.let { "$it Colorless/Generic/Any Colors," }
                    }
                }
            }
        }
        return output
    }

    /**
     * This function will format the mana color to make it more readable
     */
    private fun formatManaColor(manaColor: List<String>?): String {
        /**
         * colors":["R","U","W"]) Will need to setup condition where R = Red, B = Black, U = Blue, W = White, G = Green, and empty [] = Colorless
         */

        if (manaColor == null || manaColor.isEmpty()) {
            return "Colorless"
        }

        var output = ""

        manaColor.forEachIndexed { index, color ->
            val isLast = index == manaColor.lastIndex
            if (isLast) {
                when (color.uppercase()) {
                    "R" -> output += "and Red"
                    "B" -> output += "and Black"
                    "U" -> output += "and Blue"
                    "W" -> output += "and White"
                    "G" -> output += "and Green"
                }
            } else {
                when (color.uppercase()) {
                    "R" -> output += "Red, "
                    "B" -> output += "Black, "
                    "U" -> output += "Blue, "
                    "G" -> output += "Green, "
                    "W" -> output += "White, "
                }
            }
        }
        return output
    }


    override fun onGetCardRuleData(data: Rulings?) {

        Log.i(TAG, "Data from API for card rules data: $data")

        if(data == null){
            // Will need to save this
           // "There are no available rules"
        }

        var output = "The rulings are: "

        data?.moreData?.forEach{ruling ->
            output += ruling.comment
        }
        Log.i(TAG,output)
    }

    override fun onGetCardSetData(data: CardSet?) {
        Log.i(TAG, "Data from API for card set data: $data" )

        if(data == null){
            // "There are no listed sets
        }

        val output = "This card is from the set: ${data?.name}."
        Log.i(TAG,output)
    }
}
