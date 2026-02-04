package com.example.anew.ui.fragment.chat.friendrequest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.data.local.MyHelper
import com.example.anew.model.User
import com.example.anew.data.repo.AuthRepo
import com.example.anew.data.repo.FriendRepo
import kotlinx.coroutines.launch

class FriendsRequestViewModel(
    private val authRepo: AuthRepo,
    private val friendRepo: FriendRepo
): ViewModel() {
    private val _friendsRequest = MutableLiveData<List<User>>()
    val friendsRequest: LiveData<List<User>> = _friendsRequest

    fun fetchFriendsRequest(){
        viewModelScope.launch {
            _friendsRequest.value = friendRepo.getFriendsRequest(MyHelper.user!!.uid).map {
                authRepo.getUserDataFromUid(it)
            }
        }
    }

    fun removeRequestFriend(uidRequestFriend: String) {
        viewModelScope.launch {
            friendRepo.removeRequestFriend(MyHelper.user!!.uid, uidRequestFriend)
            _friendsRequest.value = _friendsRequest.value.filter { it.uid != uidRequestFriend }
        }
    }

    fun addFriend(requestFriend: User) = viewModelScope.launch {
        friendRepo.addFriend(MyHelper.user!!.uid, requestFriend)
        _friendsRequest.value = _friendsRequest.value.filter { it.uid != requestFriend.uid }
    }

    suspend fun checkSearchFriend(email: String): Boolean = authRepo.checkEmailExist(email)

    suspend fun getUidDataFromEmail(email: String): String = authRepo.getUidDataFromEmail(email)
}