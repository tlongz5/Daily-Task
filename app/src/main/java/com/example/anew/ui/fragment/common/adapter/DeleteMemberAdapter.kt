package com.example.anew.ui.fragment.common.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemDeleteMemberBinding
import com.example.anew.model.User

class DeleteMemberAdapter(
    private val adminId: String,
    private val callback: (User) -> Unit
) : ListAdapter<User, DeleteMemberAdapter.ViewHolder>(DiffCallback) {
    class ViewHolder(val binding: ItemDeleteMemberBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemDeleteMemberBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        if(item.uid==adminId) holder.binding.tvAdmin.visibility= View.VISIBLE
        else holder.binding.tvAdmin.visibility=View.GONE

        holder.binding.tvNameMember.text = item.name
        holder.binding.tvUsername.text = item.username
        Glide.with(holder.binding.root.context)
            .load(item.photoUrl)
            .circleCrop()
            .into(holder.binding.avatar)

        holder.itemView.setOnClickListener {
            callback(item)
        }
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<User>() {
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
}