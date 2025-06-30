package com.mashaffer.mymtgchatbot


import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Util : ViewModel() {
    private val scryfallApi =
        ScryfallApiServiceHelper.getInstance().create(ScryfallAPIServiceInterface::class.java)

    companion object {
        private const val TAG = "Util"
        @SuppressLint("StaticFieldLeak")
        private val mainActivity: MainActivity = MainActivity()
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
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardData.postValue(CardResponse.CardError("Card Not Found. Please check spelling", question, fromSpeech))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for General Card Data: ${e.message}")
                cardData.postValue(CardResponse.CardError("Card Not Found. Please check spelling", question, fromSpeech))
            }
        }
    }


    fun getCardRuleData(cardID: String, question: String?) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardRuleData(cardID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful Card Rules API Call: ${result.body()}")
                    cardRuleData.postValue(RulingResponse.RulingData(result.body(), question))
                } else {
                    Log.i(TAG, "Error getting Card Rules Information: ${result.code()}")
                    cardRuleData.postValue(RulingResponse.RulingError("Card Not Found. Please check spelling", question))
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Additional Rules: ${e.message}")
                cardRuleData.postValue(RulingResponse.RulingError("Card Not Found. Please check spelling", question))
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
                    Log.i(TAG, "Error getting Card Information: ${result.code()}")
                    cardSetData.postValue(SetResponse.SetError("Card Not Found. Please check spelling", question))
                    withContext(Dispatchers.Main) {
                        mainActivity.updateChat(result.message(),question)
                    }
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call for Set Information: ${e.message}")
                cardSetData.postValue(SetResponse.SetError("Card Not Found. Please check spelling", question))
                withContext(Dispatchers.Main) {
                    mainActivity.updateChat(e.toString(), question)
                }

            }
        }
    }
}
