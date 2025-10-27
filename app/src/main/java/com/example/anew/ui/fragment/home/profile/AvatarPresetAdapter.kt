package com.example.anew.ui.fragment.home.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemAvatarPresetBinding
import com.example.anew.model.MyHelper

class AvatarPresetAdapter(private val onItemClicked: (position: Int) -> Unit): RecyclerView.Adapter<AvatarPresetAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemAvatarPresetBinding): RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemAvatarPresetBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        Glide.with(holder.itemView.context)
            .load(MyHelper.avatar[position])
            .circleCrop()
            .into(holder.binding.avatar)

        holder.binding.root.setOnClickListener {
            onItemClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return MyHelper.avatar.size
    }

}