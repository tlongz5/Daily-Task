package com.example.anew.viewmodelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.ProjectRepo
import com.example.anew.ui.activity.login.AuthViewModel
import com.example.anew.ui.fragment.home.HomeViewModel

class MyViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(AuthRepo()) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(ProjectRepo()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}