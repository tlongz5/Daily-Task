package com.example.anew.ui.fragment.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemFriendListBinding
import com.example.anew.model.User

class FriendListAdapter(private var friends: MutableList<User>,
    private val onClickJohnChatRoom: (String) -> Unit,
    private val onClickViewProfileUser: (String) -> Unit) : RecyclerView.Adapter<FriendListAdapter.FriendListViewHolder>() {
    class FriendListViewHolder(val binding: ItemFriendListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendListViewHolder {
        val binding = ItemFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FriendListViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: FriendListViewHolder,
        position: Int
    ) {
        holder.binding.tvName.text= friends[position].name
        holder.binding.chat.setOnClickListener {
            onClickJohnChatRoom(friends[position].uid)
        }
        holder.binding.avatar.setOnClickListener {
            onClickViewProfileUser(friends[position].uid)
        }
        holder.itemView.setOnClickListener {
            onClickViewProfileUser(friends[position].uid)
        }
        Glide.with(holder.itemView.context)
            .load(friends[position].photoUrl)
            .circleCrop()
            .into(holder.binding.avatar)

    }

    override fun getItemCount(): Int {
        return friends.size
    }

    fun updateData(newFriends: List<User>) {
        friends.clear()
        friends.addAll(newFriends)
        notifyDataSetChanged()
    }
}
