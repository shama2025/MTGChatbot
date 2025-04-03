package com.mashaffer.mymtgchatbot

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.Callback

class Util: ViewModel() {
    val scryfallApi =
        ScryfallApiServiceHelper.getInstance().create(ScryfallAPIServiceInterface::class.java)

    companion object {
        private const val TAG = "Util"
    }

    fun getCardData(callback: MainActivity, cardName: String) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardGenData(cardName)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call: ${e.message}")

            }
        }
    }

    fun getCardRuleData(callback: MainActivity, cardID: String) {
        viewModelScope.launch {
            try {
                val result = scryfallApi.getCardRuleData(cardID)
                if (result.isSuccessful) {
                    Log.i(TAG, "Successful API Call: ${result.body()}")
                }
            } catch (e: Exception) {
                Log.i(TAG, "Failed API Call: ${e.message}")

            }
        }
    }
}