package com.example.anew.ui.fragment.chat.friend_request

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.User
import com.example.anew.support.fakeData
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.FriendRepo
import kotlinx.coroutines.launch

class FriendsRequestViewModel(
    private val authRepo: AuthRepo,
    private val friendRepo: FriendRepo
): ViewModel() {
    private val _friendsRequest = MutableLiveData<List<User>>()
    val friendsRequest: LiveData<List<User>> = _friendsRequest

    fun fetchFriendsRequest(){
        viewModelScope.launch {
            _friendsRequest.value = friendRepo.getFriendsRequest(fakeData.user!!.uid).map {
                authRepo.getUserDataFromUid(it)
            }
        }
    }

    fun removeRequestFriend(uidRequestFriend: String) {
        viewModelScope.launch {
            friendRepo.removeRequestFriend(fakeData.user!!.uid, uidRequestFriend)
            _friendsRequest.value = _friendsRequest.value.filter { it.uid != uidRequestFriend }
        }
    }

    fun addFriend(uidRequestFriend: String) = viewModelScope.launch {
        friendRepo.addFriend(fakeData.user!!.uid, uidRequestFriend)
        _friendsRequest.value = _friendsRequest.value.filter { it.uid != uidRequestFriend }
    }

    suspend fun checkSearchFriend(email: String): Boolean = authRepo.checkEmailExist(email)

    suspend fun getUidDataFromEmail(email: String): String = authRepo.getUidDataFromEmail(email)
}