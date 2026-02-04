package com.example.anew.data.local

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.anew.model.User

// save to share pref and fake data
fun saveUserToSharePrefAndDataLocal(user: User, context: Context){
    val sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("uid", user.uid)
    editor.putString("name", user.name)
    editor.putString("email", user.email)
    editor.putString("avatar", user.photoUrl)
    editor.putString("phoneNumber",user.phoneNumber)
    editor.putString("username", user.username)
    editor.apply()

    MyHelper.user = user
}

fun getUserFromSharePref(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)
}

fun deleteUserFromSharePref(context: Context){
    val sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)
    sharedPreferences.edit().clear().apply()
    MyHelper.user = null
}

fun updateAvatarFromSharePref(context: Context, url: String){
    val sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)
    sharedPreferences.edit().putString("avatar",url).apply()
    MyHelper.user!!.photoUrl = url
}