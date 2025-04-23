package com.mashaffer.mymtgchatbot

import Card
import Rulings
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val cardSetData = MutableLiveData<CardSetResponse?>()

    fun getCardData(cardName: String?, question: String? ,fromSpeech: Boolean) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardGenData(cardName)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardData.postValue(CardResponse(result.body(), question,fromSpeech))
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardData.postValue(CardResponse(null, question,fromSpeech))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for General Card Data: ${e.message}")
                cardData.postValue(CardResponse(null, question,fromSpeech))
            }
        }
    }


    fun getCardRuleData(cardID: String, question: String?) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardRuleData(cardID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardRuleData.postValue(RulingResponse(result.body(),question))
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardRuleData.postValue(RulingResponse(null,question))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Additional Rules: ${e.message}")
                cardRuleData.postValue(RulingResponse(null,question))
            }
        }
    }

    fun getCardSetData(setID: String,question: String?) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardSetData(setID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardSetData.postValue(CardSetResponse(result.body(),question))
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardSetData.postValue(CardSetResponse(null,question))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Set Information: ${e.message}")
                cardSetData.postValue(CardSetResponse(null,question))
            }
        }
    }
}
