package com.mashaffer.mymtgchatbot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserChatAdapter(private val userChats: String) : RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_chat_box_layout, parent, false)
        return UserChatViewHolder(view)
    }

    override fun getItemCount(): Int = userChats.length

    override fun onBindViewHolder(holder: UserChatViewHolder, position: Int) {
        holder.bind(userChats)
    }

    inner class UserChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatTextView: TextView = itemView.findViewById(R.id.userChatBox)

        fun bind(chat: String) {
            chatTextView.text = chat
        }
    }
}
