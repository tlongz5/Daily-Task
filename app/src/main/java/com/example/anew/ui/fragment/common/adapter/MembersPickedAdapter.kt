package com.example.anew.ui.fragment.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemMembersPickedBinding
import com.example.anew.model.User

class MembersPickedAdapter(private val callback: (User) -> Unit): ListAdapter<User, MembersPickedAdapter.ViewHolder>(DIFF_CALLBACK) {
    class ViewHolder(val binding: ItemMembersPickedBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemMembersPickedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val user = getItem(position)
    //Xu li last name truoc khi cho vao recycleview tranh phai xu li lai
        holder.binding.tvNameMember.text = user.name.split(" ").last()
        holder.binding.btnDeleteMember.setOnClickListener {
            callback(user)
        }

        Glide.with(holder.itemView.context)
            .load(user.photoUrl)
            .circleCrop()
            .into(holder.binding.imgAvatar)
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