package com.mashaffer.mymtgchatbot

interface ScryfallCallback {


    fun onGetCardData(data:String)

    fun onGetCardRuleData(data: String)
}