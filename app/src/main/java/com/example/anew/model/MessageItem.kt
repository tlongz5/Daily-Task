package com.example.anew.model

//this type to read data and show in recyclerview
data class MessageItem(
    val messageId: String,
    val senderId: String,
    val senderName: String,
    val senderAvatar: String,
    val message: String,
    val imageUrlList: List<String>,
    val time: Long
){
    constructor() : this("", "", "", "", "", emptyList(), 0)
}