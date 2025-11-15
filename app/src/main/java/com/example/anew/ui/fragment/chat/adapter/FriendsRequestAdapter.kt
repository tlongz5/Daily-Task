package com.example.anew.ui.fragment.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemFriendRequestBinding
import com.example.anew.model.User

class FriendsRequestAdapter(private val friendsRequestList: MutableList<User>,
    private val callbackAccept: (String) -> Unit,
    private val callbackDecline: (String) -> Unit): RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemFriendRequestBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendsRequestAdapter.ViewHolder {
        val binding = ItemFriendRequestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendsRequestAdapter.ViewHolder, position: Int) {
        val friendRequest = friendsRequestList[position]

        with(holder.binding){
            tvName.text = friendRequest.name
            tvUsername.text = friendRequest.name
            btnAccept.setOnClickListener {
                callbackAccept(friendRequest.uid)
            }
            btnDecline.setOnClickListener {
                callbackDecline(friendRequest.uid)
            }
            avatar.setOnClickListener {

            }
            Glide.with(holder.itemView.context)
                .load(friendRequest.photoUrl)
                .circleCrop()
                .into(avatar)
        }
    }

    override fun getItemCount(): Int {
        return friendsRequestList.size
    }

    fun setChangeFriendRequestList(newFriendsRequestList: List<User>){
        friendsRequestList.clear()
        friendsRequestList.addAll(newFriendsRequestList)
        notifyDataSetChanged()
    }

}