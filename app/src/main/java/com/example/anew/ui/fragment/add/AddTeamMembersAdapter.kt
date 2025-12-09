package com.example.anew.ui.fragment.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemAddMemberBinding
import com.example.anew.model.Team
import com.example.anew.model.User
import com.example.anew.support.fakeData

// add User to new project
class AddTeamMembersAdapter: ListAdapter<User, AddTeamMembersAdapter.ViewHolder>(diffUtil) {
    class ViewHolder(val binding: ItemAddMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAddMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.binding.tvName.text = item.name.split(" ").last()
        Glide.with(holder.itemView.context)
            .load(item.photoUrl)
            .circleCrop()
            .into(holder.binding.avatar)
    }


    companion object{
        val diffUtil = object : DiffUtil.ItemCallback<User>(){
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(
                oldItem: User,
                newItem: User
            ): Boolean {
                return oldItem==newItem
            }
        }
    }
}