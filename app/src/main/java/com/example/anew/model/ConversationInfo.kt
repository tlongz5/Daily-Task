package com.example.anew.model

data class ConversationInfo(
    val roomId: String,
    val chatName: String,
    val avatar: String,
    val adminId: String,
    val userId: List<String>
)
