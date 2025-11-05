package com.example.anew.ui.fragment.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemPickFriendBinding
import com.example.anew.model.User

class PickFriendAdapter(private val callback: (Boolean, User) -> Unit): PagingDataAdapter<User, PickFriendAdapter.ViewHolder>(DIFF_CALLBACK) {
    private val selectedFriends = mutableListOf<String>()

    class ViewHolder(val binding: ItemPickFriendBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemPickFriendBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        getItem(position)?.let { user ->
            holder.binding.tvNameMember.text = user.name
            Glide.with(holder.itemView.context)
                .load(user.photoUrl)
                .circleCrop()
                .into(holder.binding.avatar)
            setCbLogic(holder,user)
        }
    }

    private fun setCbLogic(holder: ViewHolder, user: User) {
        holder.binding.btnPickMember.setOnCheckedChangeListener(null)
        holder.binding.btnPickMember.isChecked = selectedFriends.contains(user.uid)

        holder.itemView.setOnClickListener {
            holder.binding.btnPickMember.isChecked = !holder.binding.btnPickMember.isChecked
        }
        holder.binding.btnPickMember.setOnCheckedChangeListener { _, isChecked ->
            callback(isChecked,user)
        }
    }

    fun reloadPickedFriend(users: List<String>){
        selectedFriends.clear()
        selectedFriends.addAll(users)
    }

    fun updateUserListSelection(user: User, isChecked: Boolean) {
        if (isChecked) {
            selectedFriends.add(user.uid)
        }else {
            selectedFriends.remove(user.uid)
        }
        val index = snapshot().indexOfFirst { it!!.uid == user.uid }
        notifyItemChanged(index)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }
            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem == newItem
            }
        }
    }
}