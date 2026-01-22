package com.example.anew.ui.fragment.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.ItemNotificationBinding
import com.example.anew.model.Notification
import com.example.anew.support.toTime

class NotificationAdapter(private val callback: (String,String,String, String) -> Unit): ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NOTIFICATION_DIFFUTIL) {
    class NotificationViewHolder(val binding: ItemNotificationBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationViewHolder {
       val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: NotificationViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        holder.itemView.setOnClickListener {
            callback(item.notificationId,item.type,item.projectId,item.userId)
        }

        with(holder.binding){
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvTime.text = item.time.toTime()
            Glide.with(holder.itemView.context)
                .load(item.avatar.ifEmpty { R.drawable.notifi_logo })
                .circleCrop()
                .error(R.drawable.avatar14)
                .into(imgAvatar)

            if(item.checkRead) status.visibility = View.GONE
            else status.visibility = View.VISIBLE
        }
    }

    companion object{
        val NOTIFICATION_DIFFUTIL = object : DiffUtil.ItemCallback<Notification>(){
            override fun areItemsTheSame(
                oldItem: Notification,
                newItem: Notification
            ): Boolean {
                return oldItem.notificationId == newItem.notificationId
            }

            override fun areContentsTheSame(
                oldItem: Notification,
                newItem: Notification
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}