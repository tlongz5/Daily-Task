package com.example.anew.ui.fragment.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Notification
import com.example.anew.data.repo.NotificationRepo
import kotlinx.coroutines.launch

class NotificationViewModel(private val notificationRepo: NotificationRepo): ViewModel() {
    private val _notificationList = MutableLiveData<List<Notification>>()
    val notificationList: LiveData<List<Notification>> = _notificationList

    fun getNotification(uid: String){
        viewModelScope.launch {
            _notificationList.value = notificationRepo.getNotification(uid).sortedByDescending { it.time }
        }
    }

    fun updateStatus(notificationId: String) {
        viewModelScope.launch {
            notificationRepo.updateStatus(notificationId)
        }
    }
}