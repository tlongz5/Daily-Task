package com.example.anew.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.ItemOngoingProjectCardBinding
import com.example.anew.model.Team
import com.example.anew.support.toFullTime
import kotlin.math.min

class OngoingProjectAdapter(private val ongoingProject: MutableList<Team>,
    val callback: (String) -> Unit): RecyclerView.Adapter<OngoingProjectAdapter.OngoingProjectViewHolder>() {
    class OngoingProjectViewHolder(val binding: ItemOngoingProjectCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OngoingProjectViewHolder {
        val binding = ItemOngoingProjectCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OngoingProjectViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: OngoingProjectViewHolder,
        position: Int
    ) {
        with(holder.binding){
            projectTitle.text = ongoingProject[position].title
            projectProgress.progress = ongoingProject[position].completedPercent
            dueTime.text = "Due on : ${ongoingProject[position].dueTime?.toFullTime()}"
            val members = ongoingProject[position].teamMembersImage
            val avatars = listOf(avt1,avt2,avt3,avt4)
            for(i in 0 until min(4, members.size)){
                avatars[i].visibility = View.VISIBLE
                if(i==3) avatars[i].setImageResource(R.drawable.ic_more)
                else {
                    Glide.with(holder.itemView.context)
                        .load(members[i])
                        .error(R.drawable.avt1)
                        .centerCrop()
                        .override(20,20)
                        .circleCrop()
                        .into(avatars[i])
                }
            }
        }
        holder.itemView.setOnClickListener {
            callback(ongoingProject[position].id)
        }
    }

    override fun getItemCount(): Int {
        return ongoingProject.size
    }
}

