package com.mashaffer.mymtgchatbot

import Rulings


data class RulingResponse(
    val rulings: Rulings?,
    val userQuery: String?
)
