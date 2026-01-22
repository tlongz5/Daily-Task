package com.example.anew.ui.fragment.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Team
import com.example.anew.repo.ProjectRepo
import kotlinx.coroutines.launch

class CalendarViewModel(private val projectRepo: ProjectRepo): ViewModel() {
    private val _teamList = MutableLiveData<List<Team>>()
    val teamList: LiveData<List<Team>> = _teamList

    fun getTeamList(userId:String){
        viewModelScope.launch {
            _teamList.value = projectRepo.getProjectsData(userId)
        }
    }
}