package com.example.anew.ui.fragment.chat.other_user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.User
import com.example.anew.support.fakeData
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.FriendRepo
import kotlinx.coroutines.launch

class OtherUserProfileViewModel(private val authRepo: AuthRepo,
    private val FriendRepo: FriendRepo
): ViewModel() {
    private val _user = MutableLiveData<User>()
    val user: LiveData<User> = _user

    private val _isFriend = MutableLiveData<Int>()
    val isFriend: LiveData<Int> = _isFriend

    fun getUserData(uid: String) = viewModelScope.launch {
        _user.value = authRepo.getUserDataFromUid(uid)
    }

    fun checkFriend(uid: String) = viewModelScope.launch {
        _isFriend.value = FriendRepo.checkFriend(fakeData.user!!.uid,uid)
    }

    ///NOTEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE/./
    fun requestFriend(friendId: String) = viewModelScope.launch {
        FriendRepo.requestFriend(fakeData.user!!.uid, friendId)
        _isFriend.value = FriendRepo.checkFriend(fakeData.user!!.uid,friendId)
    }

    fun unFriend(friendId: String) = viewModelScope.launch {
        FriendRepo.unFriend(fakeData.user!!.uid, friendId)
        _isFriend.value = -1
    }

    fun fromRequestToCancel(friendId: String) = viewModelScope.launch {
        FriendRepo.fromRequestToCancel(fakeData.user!!.uid, friendId)
        _isFriend.value = -1
    }
}