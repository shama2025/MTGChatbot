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
                    Log.i(TAG, "Error getting com.mashaffer.mymtgchatbot.model.Card Information: ${result.code()}")
                    cardData.postValue(CardResponse.CardError("${result.code()}. com.mashaffer.mymtgchatbot.model.Card Not Found. Please check spelling.", question, fromSpeech))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for General com.mashaffer.mymtgchatbot.model.Card Data: ${e.message}")
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
                    Log.i(TAG, "Error getting com.mashaffer.mymtgchatbot.model.Card Rules Information: ${result.code()}")
                    cardRuleData.postValue(RulingResponse.RulingError("${result.code()}. com.mashaffer.mymtgchatbot.model.Card Not Found. Please check spelling.", question))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Additional Rules: ${e.message}")
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
                    Log.i(TAG, "Error getting com.mashaffer.mymtgchatbot.model.Card Information: ${result.code()}")
                    cardSetData.postValue(SetResponse.SetError("${result.code()}. com.mashaffer.mymtgchatbot.model.Card Not Found. Please check spelling.", question))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Set Information: ${e.message}")
                cardSetData.postValue(SetResponse.SetError("API Error: ${e.message}", question))
            }
        }
    }
}