package com.example.anew.ui.fragment.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Team
import com.example.anew.support.fakeData
import com.example.anew.repo.ProjectRepo
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeViewModel(private val projectRepo: ProjectRepo): ViewModel() {
    private val _isCheckSwapScreen = MutableLiveData<Boolean>(false)
    val isCheckSwapScreen: LiveData<Boolean> = _isCheckSwapScreen
    private val _projectState = MutableLiveData<MutableList<Team>>()
    val projectState: LiveData<MutableList<Team>> = _projectState

    private val _completedProjectState = MutableLiveData<MutableList<Team>>()
    val completedProjectState: LiveData<MutableList<Team>> = _completedProjectState

    private val _ongoingProjectState = MutableLiveData<MutableList<Team>>()
    val ongoingProjectState: LiveData<MutableList<Team>> = _ongoingProjectState


    fun getProjectData(){
        viewModelScope.launch {
            val projectList = projectRepo.getProjectsData(fakeData.user!!.uid)
            _projectState.value = projectList
            _isCheckSwapScreen.value = true
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