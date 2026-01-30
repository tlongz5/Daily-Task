package com.example.anew.ui.fragment.add

import android.util.Printer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Conversation
import com.example.anew.model.Notification
import com.example.anew.model.Team
import com.example.anew.model.User
import com.example.anew.repo.MessageRepo
import com.example.anew.repo.ProjectRepo
import com.example.anew.support.MyHelper
import com.example.anew.support.fakeData
import com.example.anew.support.mergeDateAndTime
import kotlinx.coroutines.launch
import java.util.UUID

class AddViewModel(
    private val projectRepo: ProjectRepo,
    private val messageRepo: MessageRepo
): ViewModel() {

    private val _teamState = MutableLiveData<List<User>>()
    val teamState: LiveData<List<User>> = _teamState

    var setDate: Long?=null
    var setHour: Int?=null
    var setMinute: Int?=null
    var projectName: String?=null
    var taskDetail: String?=null

    fun createProject(team: Team){
        viewModelScope.launch {

            // chua Dam bao thuoc tinh atomic
            val notification = Notification(
                UUID.randomUUID().toString(),
                team.title,
                "Your project has been created",
                System.currentTimeMillis(),
                false,
                team.avatar,
                "create_project",
                team.projectId,
                "",
                ""
            )
//chua dam bao tính atomic
            projectRepo.createProject(team,notification)
            messageRepo.createGroup(team.projectId,team.title,team.avatar,
                fakeData.user!!.uid,team.members,"Project")
        }
    }

    fun saveUser(users: List<User>) {
        _teamState.value = users
    }

    //Noteee
//    fun mergeDateAndTime(): Long? {
//         val date = setDate.value
//         val hour = setHour.value
//         val minute = setMinute.value
//
//        if(date!=null && hour!=null && minute!=null)  return mergeDateAndTime(date, hour, minute)
//        return null
//    }

}