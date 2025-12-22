package com.example.anew.ui.fragment.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.anew.model.User
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.FriendRepo
import com.example.anew.repo.MessageRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SelectAddMemberViewModel(
    private val friendRepo: FriendRepo,
    private val messageRepo: MessageRepo,
    private val currentUserId: String
): ViewModel() {

    val friendPagingData: Flow<PagingData<User>> = Pager(
        config = PagingConfig(
            pageSize = 20,
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            LoadFriendPaging(friendRepo, currentUserId)
        }
    ).flow.cachedIn(viewModelScope)

    private val _friendPickedState = MutableLiveData<List<User>>(emptyList())
    val friendPickedState: LiveData<List<User>> = _friendPickedState

    fun updateFriendPickedState(checked:Boolean, friend: User){
        if(checked){
            _friendPickedState.value = _friendPickedState.value.plus(friend)
        }else{
            _friendPickedState.value = _friendPickedState.value.minus(friend)
        }
    }
    fun createGroup(groupName: String, avatar: String, adminId: String, users: List<String>,groupType: String){
        viewModelScope.launch {
            messageRepo.createGroup(groupName, avatar, adminId, users,groupType)
        }
    }

}