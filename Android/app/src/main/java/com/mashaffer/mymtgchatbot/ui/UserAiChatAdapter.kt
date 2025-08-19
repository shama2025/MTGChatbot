package com.mashaffer.mymtgchatbot.ui

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mashaffer.mymtgchatbot.R
import com.mashaffer.mymtgchatbot.chat.Actor
import com.mashaffer.mymtgchatbot.chat.ChatMessage

class UserAiChatAdapter : RecyclerView.Adapter<UserAiChatAdapter.AIChatViewHolder>() {

    private val chat = mutableListOf<ChatMessage>()

    fun addMessage(message: ChatMessage) {
        chat.add(message)
        notifyItemInserted(chat.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_ai_chat_layout, parent, false)
        return AIChatViewHolder(view)
    }

    override fun getItemCount(): Int = chat.size

    override fun onBindViewHolder(holder: AIChatViewHolder, position: Int) {
        val message = chat[position]
        Log.i("UserAIChatAdapter", "Binding message: ${message.role}: ${message.content}")
        holder.bind(message)
    }

    inner class AIChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val aiChatBox: TextView = itemView.findViewById(R.id.aiChatBox)
        private val userChatBox: TextView = itemView.findViewById(R.id.userChatBox)
        private val aiIcon: ImageView = itemView.findViewById(R.id.aiIcon)
        private val spinner: ProgressBar = itemView.findViewById(R.id.spinner)

        @SuppressLint("SetTextI18n")
        fun bind(message: ChatMessage) {
            Log.i("UserAIChatAdapter", "Bind was called for: ${message.role}")

            when (message.role) {
                Actor.AI -> {
                    aiChatBox.visibility = View.VISIBLE
                    userChatBox.visibility = View.GONE
                    aiIcon.visibility = View.VISIBLE
                    aiChatBox.text = message.content
                    spinner.visibility = View.GONE
                }

                Actor.USER -> {
                    userChatBox.visibility = View.VISIBLE
                    aiChatBox.visibility = View.GONE
                    aiIcon.visibility = View.GONE
                    userChatBox.text = "User: ${message.content}"
                }
            }
        }
    }
}