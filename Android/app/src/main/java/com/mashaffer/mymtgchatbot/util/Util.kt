package com.mashaffer.mymtgchatbot.util

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mashaffer.mymtgchatbot.network.ScryfallAPIServiceInterface
import com.mashaffer.mymtgchatbot.network.ScryfallApiServiceHelper
import com.mashaffer.mymtgchatbot.model.CardResponse
import com.mashaffer.mymtgchatbot.model.RulingResponse
import com.mashaffer.mymtgchatbot.model.SetResponse
import kotlinx.coroutines.launch

class Util : ViewModel() {

    private val manaColorMap: MutableMap<List<String>, String> = mutableMapOf(
        listOf("B") to "Black",
        listOf("G") to "Green",
        listOf("R") to "Red",
        listOf("U") to "Blue",
        listOf("W") to "White",

        listOf("B", "U") to "Black and Blue",
        listOf("B", "W") to "Black and White",
        listOf("B", "G") to "Black and Green",
        listOf("B", "R") to "Black and Red",
        listOf("G", "R") to "Green and Red",
        listOf("G", "U") to "Green and Blue",
        listOf("G", "W") to "Green and White",
        listOf("R", "U") to "Red and Blue",
        listOf("R", "W") to "Red and White",
        listOf("U", "W") to "Blue and White",

        listOf("B", "U", "W") to "Black, Blue, and White",
        listOf("B", "G", "U") to "Black, Blue, and Green",
        listOf("B", "R", "U") to "Black, Blue, and Red",
        listOf("B", "G", "W") to "Black, White, and Green",
        listOf("B", "R", "W") to "Black, White, and Red",
        listOf("B", "G", "R") to "Black, Green, and Red",
        listOf("G", "R", "U") to "Green, Red, and Blue",
        listOf("G", "U", "W") to "Green, Blue, and White",
        listOf("R", "U", "W") to "Red, Blue, and White",
        listOf("G", "R", "W") to "Green, Red, and White",

        listOf("B", "R", "U", "W") to "Black, Red, Blue, and White",
        listOf("B", "G", "U", "W") to "Black, Green, Blue, and White",
        listOf("G", "R", "U", "W") to "Green, Red, Blue, and White",
        listOf("B", "G", "R", "W") to "Black, Green, Red, and White",
        listOf("B", "G", "R", "U") to "Black, Green, Red, and Blue",

        listOf("B", "G", "R", "U", "W") to "Black, Green, Red, Blue, and White"
    )



    private val scryfallApi =
        ScryfallApiServiceHelper.getInstance().create(ScryfallAPIServiceInterface::class.java)

    companion object {
        private const val TAG = "Util"
    }

    // Exposed LiveData
    val cardData = MutableLiveData<CardResponse>()
    val cardRuleData = MutableLiveData<RulingResponse?>()
    val cardSetData = MutableLiveData<SetResponse>()

    fun getCardData(cardName: String?, question: String?, fromSpeech: Boolean) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardGenData(cardName)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardData.postValue(CardResponse.CardData(result.body(), question, fromSpeech))
                } else {
                    Log.e(TAG, "Error getting Card Information: ${result.code()}")
                    cardData.postValue(CardResponse.CardError("${result.code()}. Card Not Found. Please check spelling.", question, fromSpeech))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed API Call for General Card Data: ${e.message}")
                cardData.postValue(CardResponse.CardError("API Error: ${e.message}", question, fromSpeech))
            }
        }
    }


    fun getCardRuleData(cardID: String, question: String?) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardRuleData(cardID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful com.mashaffer.mymtgchatbot.model.Card Rules API Call: ${result.body()}")
                    cardRuleData.postValue(RulingResponse.RulingData(result.body(), question))
                } else {
                    Log.e(TAG, "Error getting Card Rules Information: ${result.code()}")
                    cardRuleData.postValue(RulingResponse.RulingError("${result.code()}. Card Not Found. Please check spelling.", question))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed API Call for Additional Rules: ${e.message}")
                cardRuleData.postValue(RulingResponse.RulingError("API Error: ${e.message}", question))
            }
        }
    }

    fun getCardSetData(setID: String, question: String?) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardSetData(setID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardSetData.postValue(SetResponse.SetData(result.body(), question))
                } else {
                    Log.e(TAG, "Error getting Card Set Information: ${result.code()}")
                    cardSetData.postValue(SetResponse.SetError("${result.code()}. Card Not Found. Please check spelling.", question))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed API Call for Set Information: ${e.message}")
                cardSetData.postValue(SetResponse.SetError("API Error: ${e.message}", question))
            }
        }
    }

    fun formatManaColorText( manaColor: List<String>?): String{
        return if (manaColorMap.containsKey(manaColor)) manaColorMap[manaColor].toString() else "Colorless"
    }
}