package com.example.anew.ui.fragment.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.databinding.ItemMessageBinding
import com.example.anew.model.Message

class MessageAdapter(val messages: List<Message>): RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    class MessageViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int
    ) {
        with(holder.binding){
            tvSenderMessage.text=messages[position].lastMessage
            tvSenderName.text=messages[position].name
            tvLastMessageTime.text=messages[position].lastMessage
            imgSenderAvatar.setImageResource(messages[position].avatar)
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }
}