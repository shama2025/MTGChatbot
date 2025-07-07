package com.mashaffer.mymtgchatbot.model

import Rulings

sealed class RulingResponse{
    data class RulingData(
        val rulings: Rulings?,
        val userQuery: String?
    ):RulingResponse()
    data class RulingError( val errorMsg: String,
                            val userQuery: String?):RulingResponse()
}

