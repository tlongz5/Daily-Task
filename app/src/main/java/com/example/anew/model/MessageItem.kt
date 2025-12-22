package com.example.anew.model

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