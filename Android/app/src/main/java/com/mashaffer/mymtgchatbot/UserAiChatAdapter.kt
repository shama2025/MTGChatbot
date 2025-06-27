package com.mashaffer.mymtgchatbot

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class UserAiChatAdapter(private val chat: List<ChatMessage>) :
    RecyclerView.Adapter<UserAiChatAdapter.AIChatViewHolder>() {

    companion object {
        private const val TAG = "UserAIChatAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIChatViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_ai_chat_layout, parent, false)
        return AIChatViewHolder(view)
    }

    override fun getItemCount(): Int = chat.size

    override fun onBindViewHolder(holder: AIChatViewHolder, position: Int) {
        val message = chat[position]
        Log.i(TAG, "Binding message: ${message.role}: ${message.content}")
        holder.bind(message)
    }

    inner class AIChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val aiChatBox: TextView = itemView.findViewById(R.id.aiChatBox)
        private val userChatBox: TextView = itemView.findViewById(R.id.userChatBox)
        private val aiIcon: ImageView = itemView.findViewById(R.id.aiIcon)

        fun bind(message: ChatMessage) {
            Log.i(TAG, "Bind was called for: ${message.role}")

            // Show/hide views based on role
            when (message.role) {
                Actor.AI -> {
                    aiChatBox.visibility = View.VISIBLE
                    userChatBox.visibility = View.GONE
                    aiIcon.visibility = View.VISIBLE
                    aiChatBox.text = message.content
                }

                Actor.USER -> {
                    userChatBox.visibility = View.VISIBLE
                    aiChatBox.visibility = View.GONE
                    aiIcon.visibility = View.GONE
                    userChatBox.text = "Me: ${message.content}"
                }
            }
        }
    }
}
