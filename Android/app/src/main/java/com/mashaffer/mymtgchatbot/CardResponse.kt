package com.mashaffer.mymtgchatbot

import Card

// Data class for card response
data class CardResponse(
    val card: Card?,
    val question: String?,
    val additionalInfo: Boolean
)
