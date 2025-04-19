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
    val cardRuleData = MutableLiveData<Rulings?>()
    val cardSetData = MutableLiveData<CardSet?>()

    fun getCardData(cardName: String?, fromSpeech: Boolean) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardGenData(cardName)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardData.postValue(CardResponse(result.body(), fromSpeech))
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardData.postValue(CardResponse(null, fromSpeech))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for General Card Data: ${e.message}")
                cardData.postValue(CardResponse(null, fromSpeech))
            }
        }
    }


    fun getCardRuleData(cardID: String) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardRuleData(cardID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardRuleData.postValue(result.body())
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardRuleData.postValue(null)
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Additional Rules: ${e.message}")
                cardRuleData.postValue(null)
            }
        }
    }

    fun getCardSetData(setID: String) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardSetData(setID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                    cardSetData.postValue(result.body())
                } else {
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardSetData.postValue(null)
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Set Information: ${e.message}")
                cardSetData.postValue(null)
            }
        }
    }
}
