package com.example.anew.ui.fragment.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.databinding.ItemAddMemberBinding
import com.example.anew.model.fakeData

// add User to new project
class AddTeamMembersAdapter: RecyclerView.Adapter<AddTeamMembersAdapter.ViewHolder>() {
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
        holder.binding.tvName.text = fakeData.members[position].name
        holder.binding.avatar.setImageResource(fakeData.members[position].avatar)
    }

    override fun getItemCount(): Int {
        return fakeData.members.size
    }
}