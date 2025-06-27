package com.mashaffer.mymtgchatbot

// Data class for card set response
data class CardSetResponse(
    val set: CardSet?,
    val userQuery: String?
)
