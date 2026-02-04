package com.example.anew.ui.fragment.home.taskdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.ConversationInfo
import com.example.anew.model.Team
import com.example.anew.model.User
import com.example.anew.data.repo.AuthRepo
import com.example.anew.data.repo.MessageRepo
import com.example.anew.data.repo.ProjectRepo
import com.example.anew.data.local.DataTranfer
import com.example.anew.data.local.MyHelper
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TaskDetailViewModel(
    private val authRepo: AuthRepo,
    private val projectRepo: ProjectRepo,
    private val messageRepo: MessageRepo
) : ViewModel() {

    private var _projectState = MutableLiveData<Team>()
    val projectState: LiveData<Team> = _projectState

    private var _adminState = MutableLiveData<User>()
    val adminState: LiveData<User> = _adminState

    private var _membersState = MutableLiveData<List<User>>()
    val membersState: LiveData<List<User>> = _membersState

    private var _conversationState = MutableLiveData<ConversationInfo>()
    val conversationState: LiveData<ConversationInfo> = _conversationState


    //delete AdminId from list before load
    fun getUserDataFromUid(AdminId: String, membersUid: List<String>) {
        viewModelScope.launch {
            _adminState.value = async { getUserExístOrSaveToGlobal(AdminId) }.await()
            _membersState.value = async {
                membersUid.filter { it != AdminId }
                    .map { async { getUserExístOrSaveToGlobal(it) }.await() }
            }.await()
        }
    }

    //get User from cache or Save if not exist
    private suspend fun getUserExístOrSaveToGlobal(uid: String): User {
        if (DataTranfer.userCache.containsKey(uid))
            return DataTranfer.userCache[uid]!!

        val user = authRepo.getUserDataFromUid(uid)
        DataTranfer.userCache[uid] = user
        return user
    }

    fun getProjectData(id: String) {
        viewModelScope.launch {
            _projectState.value = projectRepo.getProjectFromId(id)
        }
    }

    fun getImgGroupData(id: String) {
        viewModelScope.launch {
            _conversationState.value = messageRepo.getConversationInfo(id)
        }
    }

    fun updateProgress(isChecked: Boolean){
        viewModelScope.launch {
            val newUpdateTeam = projectRepo.updateProgress(projectState.value!!.projectId, isChecked, MyHelper.user!!.uid)
            if(newUpdateTeam!=null) {
                _projectState.value = newUpdateTeam
            }
        }
    }
}