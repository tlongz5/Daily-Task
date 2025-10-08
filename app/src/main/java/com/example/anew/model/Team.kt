package com.example.anew.model

import com.google.firebase.Timestamp

data class Team( // represent each project
    val title: String,
    val members:List<String>,
    val completedPercent:Int,
    val dueTime: Timestamp,
    val inProgress: Boolean
)
