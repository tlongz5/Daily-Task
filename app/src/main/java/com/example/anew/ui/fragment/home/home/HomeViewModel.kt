package com.example.anew.ui.fragment.home.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Team
import com.example.anew.support.fakeData
import com.example.anew.repo.ProjectRepo
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
            val currentTime = System.currentTimeMillis()

            val listToUpdate = mutableListOf<Team>()
            val updatedList = projectList.map { it ->
                if(it.inProgress && (it.dueTime!! <= currentTime)){
                    val project = it.copy(inProgress = false)
                    listToUpdate.add(project)
                    project
                }else it
            }

            //update firebase
            projectRepo.updateStatusProject(listToUpdate)

            _projectState.value = updatedList.sortedBy { it.dueTime }.toMutableList()
            _isCheckSwapScreen.value = true
        }
    }

    fun reloadProjectDataWithSearch(text: String){
        _completedProjectState.value = projectState.value.filter { !it.inProgress && checkTextData(it,text) } as MutableList<Team>
        _ongoingProjectState.value = projectState.value.filter { it.inProgress  && checkTextData(it,text) } as MutableList<Team>
    }

    private fun checkTextData( team: Team, text: String): Boolean {
        return team.title.contains(text)
    }

}