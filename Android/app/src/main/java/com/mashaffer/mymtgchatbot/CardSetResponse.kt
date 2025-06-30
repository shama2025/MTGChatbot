package com.mashaffer.mymtgchatbot

// Data class for card set response
sealed class SetResponse{
    data class SetData(
        val set: CardSet?,
        val userQuery: String?
    ): SetResponse()
    data class SetError(
        val errorMsg: String,
        val userQuery: String?
    ): SetResponse()
}

