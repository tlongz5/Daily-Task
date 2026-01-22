package com.example.anew.ui.fragment.calendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.R
import com.example.anew.databinding.ItemEventCalendarBinding
import com.example.anew.model.Team
import java.text.SimpleDateFormat
import java.util.Locale

class CalendarAdapter(private val callback: (String) -> Unit): ListAdapter<Team, CalendarAdapter.CalendarViewHolder>(CALENDAR_DIFF_UTIL) {
    class CalendarViewHolder(val binding: ItemEventCalendarBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CalendarViewHolder {
        val binding = ItemEventCalendarBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        return CalendarViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: CalendarViewHolder,
        position: Int
    ) {
        val item = getItem(position)
        with(holder.binding){
            tvTitle.text = item.title
            tvDescription.text = item.description
            tvPercent.text = "${item.completedPercent}%"
            circularProgress.progress = item.completedPercent
            tvTime.text = SimpleDateFormat("hh:mm", Locale.US).format(item.dueTime)
            tvAmPm.text = SimpleDateFormat("a", Locale.US).format(item.dueTime)
            tvStatus.text = if(item.inProgress) "In Progress" else if(item.completedPercent==100) "Completed" else "Failed"
            when(tvStatus.text){
                "In Progress" ->{
                    tvStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context,
                        R.color.task_in_progress_bg)
                    tvStatus.setTextColor(getColor(holder.itemView.context,com.example.anew.R.color.task_in_progress_tv))
                    circularProgress.setIndicatorColor(getColor(holder.itemView.context,com.example.anew.R.color.task_in_progress_tv))
                }
                "Completed" -> {
                    tvStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context,
                        R.color.task_success_bg)
                    tvStatus.setTextColor(getColor(holder.itemView.context,com.example.anew.R.color.task_success_tv))
                    circularProgress.setIndicatorColor(getColor(holder.itemView.context,com.example.anew.R.color.task_success_tv))
                }
                "Failed" -> {
                    tvStatus.backgroundTintList = ContextCompat.getColorStateList(holder.itemView.context,
                        R.color.task_failed_bg)
                    tvStatus.setTextColor(getColor(holder.itemView.context,com.example.anew.R.color.task_failed_tv))
                    circularProgress.setIndicatorColor(getColor(holder.itemView.context,com.example.anew.R.color.task_failed_tv))
                }
            }
        }

        holder.itemView.setOnClickListener {
            callback(item.projectId)
        }
    }

    companion object{
        val CALENDAR_DIFF_UTIL = object : DiffUtil.ItemCallback<Team>(){
            override fun areItemsTheSame(oldItem: Team, newItem: Team): Boolean {
                return oldItem.projectId == newItem.projectId
                }

            override fun areContentsTheSame(oldItem: Team, newItem: Team): Boolean {
                return oldItem == newItem
            }
        }
    }

}