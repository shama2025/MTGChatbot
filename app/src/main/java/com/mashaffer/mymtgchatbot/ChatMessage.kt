package com.mashaffer.mymtgchatbot

data class ChatMessage(
    val role: Actor,        // AI or USER
    val content: String
)

enum class Actor {
    USER, AI
}