package com.example.anew.ui.fragment.chat.friend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.data.local.MyHelper
import com.example.anew.model.User
import com.example.anew.data.repo.AuthRepo
import com.example.anew.data.repo.FriendRepo
import kotlinx.coroutines.launch

class FriendListViewModel(private val friendRepo: FriendRepo,
    private val authRepo: AuthRepo
): ViewModel() {
    private val _friends = MutableLiveData<List<User>>()
    val friends: LiveData<List<User>> = _friends

    fun fetchFriends() {
        viewModelScope.launch {
            _friends.value = (friendRepo.getFriendList(MyHelper.user!!.uid))
                .map { authRepo.getUserDataFromUid(it) }
        }
    }
}