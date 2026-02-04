package com.example.anew.model

data class PagingResult(
    val users: List<User>,
    val lastFriendId: String?
)


