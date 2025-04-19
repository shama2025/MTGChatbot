package com.mashaffer.mymtgchatbot

import Card

data class CardResponse(
    val card: Card?,
    val additionalInfo: Boolean
)
