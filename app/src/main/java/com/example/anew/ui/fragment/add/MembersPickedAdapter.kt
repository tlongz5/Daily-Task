package com.example.anew.ui.fragment.add

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemMembersPickedBinding
import com.example.anew.model.User

class MembersPickedAdapter(private val membersPicked: MutableList<User>,
    private val callback: (User) -> Unit): RecyclerView.Adapter<MembersPickedAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemMembersPickedBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMembersPickedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.binding.tvNameMember.text = membersPicked[position].name.split(" ").last()
        holder.binding.btnDeleteMember.setOnClickListener {
            callback(membersPicked[position])
        }

        Glide.with(holder.itemView.context)
            .load(membersPicked[position].photoUrl)
            .circleCrop()
            .into(holder.binding.imgAvatar)
    }

    override fun getItemCount(): Int {
        return membersPicked.size
    }
}