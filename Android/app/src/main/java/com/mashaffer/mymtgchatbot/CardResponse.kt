package com.mashaffer.mymtgchatbot

import Card

// Data class for card response
sealed class CardResponse{
    data class CardData(
        val card: Card?,
    val question: String?,
    val additionalInfo: Boolean
    ):CardResponse()
    data class CardError(val errorMsg: String ,   val question: String?,
                         val additionalInfo: Boolean): CardResponse()
}


