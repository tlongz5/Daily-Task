package com.example.anew.ui.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.data.local.MyHelper
import com.example.anew.data.repo.NotificationRepo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val notifiRepo: NotificationRepo): ViewModel() {
    val counter: StateFlow<Long> = notifiRepo.getCounter(MyHelper.user!!.uid)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun initCounter(uid: String) {
        viewModelScope.launch {
            notifiRepo.initCounter(uid)
        }
    }
}