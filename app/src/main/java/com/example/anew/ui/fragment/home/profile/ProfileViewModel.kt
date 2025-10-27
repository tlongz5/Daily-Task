package com.example.anew.ui.fragment.home.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.repo.AuthRepo
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepo: AuthRepo): ViewModel() {
    fun signOut(context: Context){
        viewModelScope.launch { authRepo.signOut(context) }
    }
}
