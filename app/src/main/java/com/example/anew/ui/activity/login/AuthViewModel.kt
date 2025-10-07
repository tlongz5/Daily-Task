package com.example.anew.ui.activity.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.User
import com.example.anew.repo.AuthRepo
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepo: AuthRepo): ViewModel()  {
    private val _authState = MutableLiveData<FirebaseUser?>()
    val authState: LiveData<FirebaseUser?> = _authState

    private val _userState = MutableLiveData<User?>()
    val userState: LiveData<User?> = _userState


    fun signInWithGoogle(idToken: String){
        viewModelScope.launch {
            val user = authRepo.signInWithGoogle(idToken)
            _authState.value = user
        }
    }

    fun signOut(context: Context){
        viewModelScope.launch {
            authRepo.signOut(context)
        }
    }

    fun getCurrentUser() : FirebaseUser? = authRepo.getCurrentUser()

    fun signInIntent(context: Context) : Intent = authRepo.getSignInIntent(context)

    fun initUser(context: Context, user: User){
        viewModelScope.launch {
            val check = authRepo.checkUser(user)
            if(!check) authRepo.initUser(context,user)
            _userState.value= user
        }
    }
}