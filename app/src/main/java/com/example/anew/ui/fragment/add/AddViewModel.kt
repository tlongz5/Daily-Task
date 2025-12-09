package com.example.anew.ui.fragment.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Team
import com.example.anew.model.User
import com.example.anew.repo.ProjectRepo
import com.example.anew.support.mergeDateAndTime
import kotlinx.coroutines.launch

class AddViewModel(
    private val projectRepo: ProjectRepo
): ViewModel() {

    private val _teamState = MutableLiveData<List<User>>()
    val teamState: LiveData<List<User>> = _teamState

    private val setDate= MutableLiveData<Long?>(null)
    private val setHour= MutableLiveData<Int?>(null)
    private val setMinute= MutableLiveData<Int?>(null)

    fun createProject(team: Team){
        viewModelScope.launch {
            projectRepo.createProject(team)
        }
    }

    //Noteee
    fun mergeDateAndTime(): Long? {
         val date = setDate.value
         val hour = setHour.value
         val minute = setMinute.value

        if(date!=null && hour!=null && minute!=null)  return mergeDateAndTime(date, hour, minute)
        return null
    }

}