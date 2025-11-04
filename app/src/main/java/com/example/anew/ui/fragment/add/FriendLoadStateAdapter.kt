package com.example.anew.ui.fragment.add

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.anew.databinding.ItemLoadStateBinding

class FriendLoadStateAdapter(
    private val retryCallback: () -> Unit
): LoadStateAdapter<FriendLoadStateAdapter.LoadStateViewHolder>() {

    class LoadStateViewHolder (
        private val binding: ItemLoadStateBinding,
        private val retryCallback: () -> Unit
    ): RecyclerView.ViewHolder(binding.root){
        init {
            binding.btnRetry.setOnClickListener {
                retryCallback()
            }
        }

        fun bind(loadState: LoadState){
            binding.progressBar.visibility = if(loadState is LoadState.Loading) View.VISIBLE else View.GONE
            binding.tvError.visibility = if(loadState is LoadState.Error) View.VISIBLE else View.GONE
            binding.btnRetry.visibility = if(loadState is LoadState.Error) View.VISIBLE else View.GONE
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LoadStateViewHolder{
        val binding = ItemLoadStateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LoadStateViewHolder(binding, retryCallback)
    }

    override fun onBindViewHolder(
        holder: LoadStateViewHolder,
        loadState: LoadState
    ) = holder.bind(loadState)

}