package com.example.anew.model

data class Message (
    val id: String,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: String,
    val avatar: Int,
    val type: GroupType
)