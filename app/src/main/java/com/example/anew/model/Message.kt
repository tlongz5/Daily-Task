package com.example.anew.model

//this type push to firebase
data class Message(
    val messageId: String,
    val senderId: String,
    val message: String,
    val imageUrlList: List<String>,
    val time: Long
){
    constructor() : this("", "", "", emptyList(), 0)
}