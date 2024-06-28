package com.app.androidmessageapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.androidmessageapp.R
import com.app.androidmessageapp.model.Message

class MessageAdapter(private val messages: List<Message>, private val flag: String) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val messageTextView: TextView = itemView.findViewById(R.id.messageTextView)
        val messageTextViewReceiver: TextView = itemView.findViewById(R.id.messageTextViewReceiver)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        if(flag == "Sender") {
            holder.messageTextView.text = messages[position].message
            holder.messageTextView.visibility =View.VISIBLE
        }else {
            holder.messageTextViewReceiver.text = messages[position].message
            holder.messageTextViewReceiver.visibility =View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}