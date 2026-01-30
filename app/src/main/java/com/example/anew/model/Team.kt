package com.example.anew.model

import android.os.Parcelable

data class Team( // represent each project
    val projectId: String,
    val title: String,
    val description: String,
    val avatar: String,

    val admin: String,
    var members: List<String>,

    //Delete it later
    val teamMembersImage: List<String>, // load image present on item max 3
    var completedPercent: Int,
    val dueTime: Long?,
    var inProgress: Boolean,
    var membersCompleted: List<String>
){
    constructor(): this("","","","","", listOf(),listOf(),0,null,false,listOf())
}
