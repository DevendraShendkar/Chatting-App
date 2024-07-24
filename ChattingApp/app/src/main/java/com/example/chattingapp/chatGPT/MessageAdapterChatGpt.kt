package com.example.chattingapp.chatGPT

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chat.chatGPT.MessageGpt
import com.example.chattingapp.R

class MessageAdapterChatGpt(private val messageList: List<MessageGpt>) : RecyclerView.Adapter<MessageAdapterChatGpt.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val chatView = LayoutInflater.from(parent.context).inflate(R.layout.chat_item, null)
        return MessageViewHolder(chatView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messageList[position]
        if (message.sentBy == MessageGpt.SENT_BY_ME) {
            holder.leftChatView.visibility = View.GONE
            holder.rightChatView.visibility = View.VISIBLE
            holder.rightTextView.text = message.message
        } else {
            holder.rightChatView.visibility = View.GONE
            holder.leftChatView.visibility = View.VISIBLE
            holder.leftTextView.text = message.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var leftChatView: LinearLayout = itemView.findViewById(R.id.left_chat_view)
        var rightChatView: LinearLayout = itemView.findViewById(R.id.right_chat_view)
        var leftTextView: TextView = itemView.findViewById(R.id.left_chat_text_view)
        var rightTextView: TextView = itemView.findViewById(R.id.right_chat_text_view)
    }
}
