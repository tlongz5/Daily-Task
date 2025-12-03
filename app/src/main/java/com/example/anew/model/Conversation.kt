package com.example.anew.model

data class Conversation (
    val roomId: String,
    val chatName: String,
    val senderId: String,
    val nameSender: String,
    val avatar: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    var isRead: Boolean
)