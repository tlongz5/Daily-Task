package com.example.anew.ui.fragment.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.anew.R
import com.example.anew.databinding.ItemCompletedProjectCardBinding
import com.example.anew.model.Team
import kotlin.math.min

class CompletedProjectAdapter(private val completedProject: MutableList<Team>,
    val callback: (String) -> Unit): RecyclerView.Adapter<CompletedProjectAdapter.CompletedProjectViewHolder>() {
    class CompletedProjectViewHolder(val binding: ItemCompletedProjectCardBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CompletedProjectViewHolder {
        val binding = ItemCompletedProjectCardBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CompletedProjectViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CompletedProjectViewHolder,
        position: Int
    ) {
        with(holder.binding){
            projectTitle.text = completedProject[position].title
            progress.progress = completedProject[position].completedPercent
            tvProgress.text = "${completedProject[position].completedPercent}%"
            
            if(progress.progress==100){
                successStatus.setImageResource(R.drawable.done)
                mainLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.task_success))
            }else {
                successStatus.setImageResource(R.drawable.not_done)
                mainLayout.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.task_failed))
            }

            val members = completedProject[position].teamMembersImage
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
            callback(completedProject[position].id)
        }
    }

    override fun getItemCount(): Int {
        return completedProject.size
    }
}