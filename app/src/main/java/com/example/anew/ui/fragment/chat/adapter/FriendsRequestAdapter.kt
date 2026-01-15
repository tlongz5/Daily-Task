package com.example.anew.ui.fragment.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemFriendRequestBinding
import com.example.anew.model.User

class FriendsRequestAdapter(
    private val callbackAccept: (String) -> Unit,
    private val callbackDecline: (String) -> Unit,
    private val callbackCheckProfile: (String) -> Unit
): ListAdapter<User, FriendsRequestAdapter.ViewHolder>(FriendsRequestDiffUtil()) {
    class ViewHolder(val binding: ItemFriendRequestBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsRequestAdapter.ViewHolder {
        val binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsRequestAdapter.ViewHolder, position: Int) {
        val friendRequest = getItem(position)

        with(holder.binding){
            tvName.text = friendRequest.name
            tvUsername.text = "@${friendRequest.username}"
            btnAccept.setOnClickListener {
                callbackAccept(friendRequest.uid)
            }
            btnDecline.setOnClickListener {
                callbackDecline(friendRequest.uid)
            }
            avatar.setOnClickListener {
                callbackCheckProfile(friendRequest.uid)
            }
            Glide.with(holder.itemView.context)
                .load(friendRequest.photoUrl)
                .circleCrop()
                .into(avatar)
        }
    }

    class FriendsRequestDiffUtil: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: User,
            newItem: User
        ): Boolean {
            return oldItem == newItem
        }
    }

}