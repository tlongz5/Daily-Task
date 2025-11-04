package com.example.anew.model

import com.example.anew.repo.FriendRepo

data class PagingResult(
    val users: List<User>,
    val lastFriendId: String?
)


