package com.example.anew.ui.fragment.home.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.repo.AuthRepo
import com.example.anew.support.convertUriToCloudinaryUrl
import kotlinx.coroutines.launch

class ProfileViewModel(private val authRepo: AuthRepo): ViewModel() {
    private val _imgState = MutableLiveData<String>()
    val imgState: LiveData<String> = _imgState

    fun signOut(context: Context){
        viewModelScope.launch { authRepo.signOut(context) }
    }

    fun updateProfile(name: String, username: String, phoneNumber: String){
        viewModelScope.launch { authRepo.updateProfile(name, username, phoneNumber) }
    }

    fun updateAvatar(imageUrl: String){
        viewModelScope.launch {
            val url = convertUriToCloudinaryUrl(imageUrl)
            authRepo.updateAvatar(url)
            _imgState.value = url
        }
    }

    fun updateAvatar(imageUri: Uri){
        viewModelScope.launch {
            val url = convertUriToCloudinaryUrl(imageUri)
            authRepo.updateAvatar(url)
            _imgState.value = url
        }
    }

    suspend fun checkDuplicateUsername(username: String): Boolean{
        return authRepo.checkDuplicateUsername(username)
    }
}
