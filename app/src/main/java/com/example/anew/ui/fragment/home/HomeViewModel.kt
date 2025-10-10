package com.example.anew.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Team
import com.example.anew.model.fakeData
import com.example.anew.repo.ProjectRepo
import kotlinx.coroutines.launch

class HomeViewModel(private val projectRepo: ProjectRepo): ViewModel() {
    private val _projectState = MutableLiveData<MutableList<Team>>()
    val projectState: LiveData<MutableList<Team>> = _projectState

    private val _completedProjectState = MutableLiveData<MutableList<Team>>()
    val completedProjectState: LiveData<MutableList<Team>> = _completedProjectState

    private val _ongoingProjectState = MutableLiveData<MutableList<Team>>()
    val ongoingProjectState: LiveData<MutableList<Team>> = _ongoingProjectState

    init {
        getProjectData()
    }

    fun getProjectData(){
        viewModelScope.launch {
            val projectList = projectRepo.getProjectsData(fakeData.uid!!)
            _projectState.value = projectList
        }
    }

    fun reloadProjectDataWithSearch(text: String){
        _completedProjectState.value = _projectState.value?.filter { it.inProgress == false && checkTextData(it,text) } as MutableList<Team>
        _ongoingProjectState.value = _projectState.value?.filter { it.inProgress == true && checkTextData(it,text) } as MutableList<Team>
    }

    private fun checkTextData( team: Team, text: String): Boolean {
        return team.title.contains(text)
    }

}