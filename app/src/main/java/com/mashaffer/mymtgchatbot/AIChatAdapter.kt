package com.mashaffer.mymtgchatbot

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AIChatAdapter(private val aiChat: String) : RecyclerView.Adapter<AIChatAdapter.AIChatViewHolder>() {

    companion object{
        private const val TAG = "AIChatAdapter"
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIChatViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ai_chat_box_layout, parent, false)
        return AIChatViewHolder(view)
    }

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: AIChatViewHolder,positon: Int) {
        Log.i(TAG, "The aiChat is: $aiChat")
        holder.bind(aiChat)
    }

    inner class AIChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chatTextView: TextView = itemView.findViewById(R.id.aiChatBox)

        fun bind(chat: String) {
            Log.i(TAG,"Bind was called")
            chatTextView.text = chat
        }
    }
}
