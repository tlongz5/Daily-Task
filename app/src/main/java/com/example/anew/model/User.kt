package com.example.anew.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    var photoUrl: String,
    val phoneNumber: String
){
    constructor() : this("","","","","")
}
