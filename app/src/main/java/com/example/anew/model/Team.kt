package com.example.anew.model

import com.google.firebase.Timestamp

data class Team( // represent each project
    val title: String,
    val description: String,
    val members:List<String>,
    val teamMembersImage: List<String>, // load image present on item max 3
    val completedPercent:Int,
    val dueTime: Long,
    val inProgress: Boolean
)
