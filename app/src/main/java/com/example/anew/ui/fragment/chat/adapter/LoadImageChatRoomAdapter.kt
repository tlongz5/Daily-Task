package com.example.anew.ui.fragment.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.databinding.ItemLoadImageChatRoomBinding

class LoadImageChatRoomAdapter(private val listImgUrl: List<String>): RecyclerView.Adapter<LoadImageChatRoomAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemLoadImageChatRoomBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LoadImageChatRoomAdapter.ViewHolder {
        val binding = ItemLoadImageChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: LoadImageChatRoomAdapter.ViewHolder,
        position: Int
    ) {
        Glide.with(holder.itemView.context)
            .load(listImgUrl[position])
            .into(holder.binding.image)
    }

    override fun getItemCount(): Int {
        return listImgUrl.size
    }
}