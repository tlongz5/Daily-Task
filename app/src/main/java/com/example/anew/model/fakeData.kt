package com.example.anew.model

import com.example.anew.R
import com.google.firebase.Timestamp

object fakeData{

        var uid:String? =null
        var name:String? =null
        var email:String? =null
        var avatarUrl:String? =null

        val members: List<Member> = listOf(
            Member(R.drawable.avt1, "david cot"),
            Member(R.drawable.avt2,"david cot"),
            Member(R.drawable.avt1,"david cot"),
            Member(R.drawable.avt1, "david cot"),
            Member(R.drawable.avt2,"david cot"),
            Member(R.drawable.avt1,"david cot"),
            Member(R.drawable.avt1, "david cot"),
            Member(R.drawable.avt2,"david cot"),
            Member(R.drawable.avt1,"david cot"),
            Member(R.drawable.avt1, "david cot"),
            Member(R.drawable.avt2,"david cot"),
            Member(R.drawable.avt1,"david cot")
        )

        val Messages = listOf(
            Message("user_101", "Olivia Anna", "Hi, please check the last task, that I....", "31 min", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_102", "Emna", "Hi, please check the last task, that I....", "43 min", R.drawable.avt2, GroupType.GROUP),
            Message("user_103", "Robert Brown", "Hi, please check the last task, that I....", "6 Nov", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_104", "James", "Hi, please check the last task, that I....", "8 Dec", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_105", "Sophia", "Hi, please check the last task, that I....", "27 Dec", R.drawable.avt2, GroupType.PROJECT),
            Message("user_106", "Isabella", "Hi, please check the last task, that I....", "31 min", R.drawable.avt2, GroupType.GROUP),
            Message("user_107", "Olivia Anna", "Hi, please check the last task, that I....", "31 min", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_108", "Emna", "Hi, please check the last task, that I....", "43 min", R.drawable.avt2, GroupType.GROUP),
            Message("user_113", "Robert Brown", "Hi, please check the last task, that I....", "6 Nov", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_114", "James", "Hi, please check the last task, that I....", "8 Dec", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_115", "Sophia", "Hi, please check the last task, that I....", "27 Dec", R.drawable.avt2, GroupType.PROJECT),
            Message("user_116", "Isabella", "Hi, please check the last task, that I....", "31 min", R.drawable.avt2, GroupType.GROUP),
            Message("user_111", "Olivia Anna", "Hi, please check the last task, that I....", "31 min", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_112", "Emna", "Hi, please check the last task, that I....", "43 min", R.drawable.avt2, GroupType.GROUP),
            Message("user_123", "Robert Brown", "Hi, please check the last task, that I....", "6 Nov", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_124", "James", "Hi, please check the last task, that I....", "8 Dec", R.drawable.avt1, GroupType.PERSONAL),
            Message("user_125", "Sophia", "Hi, please check the last task, that I....", "27 Dec", R.drawable.avt2, GroupType.PROJECT),
            Message("user_126", "Isabella", "Hi, please check the last task, that I....", "31 min", R.drawable.avt2, GroupType.GROUP)
        )

        val avatar = listOf(
            R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
            R.drawable.avatar9,
            R.drawable.avatar10,
            R.drawable.avatar11,
            R.drawable.avatar12,
            R.drawable.avatar13,
            R.drawable.avatar14,
            R.drawable.avatar15,
            R.drawable.avatar16,
            R.drawable.avatar17
        )



}
