package com.mashaffer.mymtgchatbot

import Card

data class CardResponse(
    val card: Card?,
    val question: String?,
    val additionalInfo: Boolean
)
