package com.example.anew.viewmodelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.FriendRepo
import com.example.anew.repo.ProjectRepo
import com.example.anew.ui.activity.login.LoginViewModel
import com.example.anew.ui.fragment.add.AddViewModel
import com.example.anew.ui.fragment.add.SelectAddMemberViewModel
import com.example.anew.ui.fragment.home.HomeViewModel
import com.example.anew.ui.fragment.home.profile.ProfileViewModel

class MyViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(AuthRepo()) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(ProjectRepo()) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(AuthRepo()) as T
            }
            modelClass.isAssignableFrom(SelectAddMemberViewModel::class.java) -> {
                SelectAddMemberViewModel(FriendRepo(), AuthRepo()) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(ProjectRepo()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}