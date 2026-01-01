package com.example.anew.model

import android.os.Parcelable

data class Team( // represent each project
    val id: String,
    val title: String,
    val description: String,
    val admin: String,
    val members: List<String>,
    val teamMembersImage: List<String>, // load image present on item max 3
    val completedPercent: Int,
    val dueTime: Long?,
    val inProgress: Boolean
){
    constructor(): this("","","","", listOf(),listOf(),0,null,false)
}
