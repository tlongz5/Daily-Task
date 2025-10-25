package com.example.anew.model

import android.content.Context
import android.content.Context.MODE_PRIVATE

fun saveUserToSharePref(user: User, context: Context){
    val sharedPreferences = context.getSharedPreferences("user", MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("uid", user.uid)
    editor.putString("name", user.name)
    editor.putString("email", user.email)
    editor.putString("avatar", user.photoUrl)
    editor.putString("phoneNumber",user.phoneNumber)
    editor.apply()
}

fun getUserFromSharePref(context: Context) {
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

}

fun editUserToSharePref(user: User, context: Context){
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)
}

fun deleteUserFromSharePref(context: Context){
    val sharedPreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE)

}