package com.example.anew.ui.fragment.chat.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemChatRoomBinding
import com.example.anew.databinding.ItemChatRoomSenderBinding
import com.example.anew.model.Message
import com.example.anew.support.fakeData
import com.example.anew.support.toTime

class ChatRoomAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffUtil()) {
    companion object{
        const val MESSAGE_TYPE_SENDER = 1
        const val MESSAGE_TYPE_RECEIVER = 0
    }

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        if (message.senderId == fakeData.user!!.uid) {
            return MESSAGE_TYPE_SENDER
        } else {
            return MESSAGE_TYPE_RECEIVER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            MESSAGE_TYPE_RECEIVER -> {
                val binding = ItemChatRoomBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                ReceiverViewHolder(binding,viewPool)
            }
            else -> {
                val binding = ItemChatRoomSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SenderViewHolder(binding,viewPool)
            }
        }

    }

    class SenderViewHolder(
        val binding: ItemChatRoomSenderBinding,
        private val viewPool: RecyclerView.RecycledViewPool
    ) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(message: Message){
                if(message.imageUrlList.isEmpty()) {
                    binding.rcvImage.visibility = View.GONE
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.tvTime.visibility = View.VISIBLE
                    binding.tvMessage.text = message.message
                    binding.tvTime.text = message.time.toTime()
                }
                else{
                    binding.tvMessage.visibility= View.GONE
                    binding.tvTime.visibility= View.GONE
                    binding.rcvImage.visibility = View.VISIBLE
                    binding.rcvImage.setRecycledViewPool(viewPool)
                    binding.rcvImage.adapter = LoadImageChatRoomAdapter(message.imageUrlList)
                    binding.rcvImage.layoutManager = GridLayoutManager(binding.root.context, 2)
                }
            }
        }

    class ReceiverViewHolder(
        val binding: ItemChatRoomBinding,
        private val viewPool: RecyclerView.RecycledViewPool
    ) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(message: Message){
                binding.tvName.text = message.senderName
                Glide.with(binding.root.context)
                    .load(message.senderAvatar)
                    .circleCrop()
                    .into(binding.imgReceiverAvatar)
                if(message.imageUrlList.isEmpty()){
                    binding.rcvImage.visibility = View.GONE
                    binding.tvMessage.visibility = View.VISIBLE
                    binding.tvTime.visibility = View.VISIBLE
                    binding.tvMessage.text = message.message
                    binding.tvTime.text = message.time.toTime()
                }else{
                    binding.tvMessage.visibility= View.GONE
                    binding.tvTime.visibility= View.GONE
                    binding.rcvImage.visibility = View.VISIBLE
                    binding.rcvImage.setRecycledViewPool(viewPool)
                    binding.rcvImage.adapter = LoadImageChatRoomAdapter(message.imageUrlList)
                    binding.rcvImage.layoutManager = GridLayoutManager(binding.root.context, 2)
                }
            }
        }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        when (holder) {
            is SenderViewHolder -> {
                holder.bind(item)
            }
            is ReceiverViewHolder -> {
                holder.bind(item)
            }
        }
    }

    class MessageDiffUtil : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(
            oldItem: Message,
            newItem: Message
        ): Boolean {
            return oldItem == newItem
        }
    }
}