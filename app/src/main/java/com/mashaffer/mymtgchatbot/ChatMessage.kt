package com.mashaffer.mymtgchatbot

// Data class to hold chat messages
data class ChatMessage(
    val role: Actor,        // AI or USER
    val content: String
)

enum class Actor {
    USER, AI
}