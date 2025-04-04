package com.mashaffer.mymtgchatbot

import Card
import Rulings

interface ScryfallCallback {


    fun onGetCardData(data:Card?)

    fun onGetCardRuleData(data: Rulings?)

    fun onGetCardSetData(data: CardSet?)
}