package com.example.anew.ui.activity.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.NotificationRepo
import com.example.anew.support.fakeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val notifiRepo: NotificationRepo): ViewModel() {
    val counter: StateFlow<Long> = notifiRepo.getCounter(fakeData.user!!.uid)
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    fun initCounter(uid: String) {
        viewModelScope.launch {
            notifiRepo.initCounter(uid)
        }
    }
}