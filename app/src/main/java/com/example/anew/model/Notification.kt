package com.example.anew.model

data class Notification(
    val notificationId:String,
    val title:String,
    val description:String,
    val time:Long,
    val checkRead:Boolean,
    val avatar:String,

    val type: String, //request friend, become friend, notify create project, notify end project
    val projectId:String, // save projectId if type = create project or end project
    val userId:String,  // person received notify
    val friendId:String  // save friendId if type = become friend
){
    constructor():this("","","",0,false,"","","","", "")
}
