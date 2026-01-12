package com.example.anew.ui.fragment.chat.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemMessageBinding
import com.example.anew.model.Conversation
import com.example.anew.support.toRelativeTime

class ConversationAdapter(private val callback: (Conversation) -> Unit): ListAdapter<Conversation, ConversationAdapter.ConversationViewHolder>(ConversationDiffUtil()) {
    class ConversationViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConversationViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ConversationViewHolder,
        position: Int
    ) {
        val item=getItem(position)
        with(holder.binding){
            tvSenderMessage.text=item.lastMessage
            tvSenderName.text=item.chatName
            tvLastMessageTime.text=item.lastMessageTime.toRelativeTime()
            if(!item.checkRead) tvSenderMessage.setTypeface(null, Typeface.BOLD)
            else tvSenderMessage.setTypeface(null, Typeface.NORMAL)
            Glide.with(holder.itemView.context)
                .load(item.avatar)
                .circleCrop()
                .into(imgSenderAvatar)
        }

        holder.itemView.setOnClickListener {
            callback(item)
        }
    }

    class ConversationDiffUtil: DiffUtil.ItemCallback<Conversation>() {
        override fun areItemsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
            oldItem.roomId == newItem.roomId
        override fun areContentsTheSame(oldItem: Conversation, newItem: Conversation): Boolean =
            oldItem == newItem
    }

}