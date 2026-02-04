package com.example.anew.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anew.data.repo.AuthRepo
import com.example.anew.data.repo.FriendRepo
import com.example.anew.data.repo.MessageRepo
import com.example.anew.data.repo.NotificationRepo
import com.example.anew.data.repo.ProjectRepo
import com.example.anew.ui.activity.login.LoginViewModel
import com.example.anew.ui.activity.main.MainViewModel
import com.example.anew.ui.fragment.add.add.AddViewModel
import com.example.anew.ui.fragment.common.selectaddmember.SelectAddMemberViewModel
import com.example.anew.ui.fragment.calendar.CalendarViewModel
import com.example.anew.ui.fragment.common.chatroom.ChatRoomViewModel
import com.example.anew.ui.fragment.chat.conversation.ConversationViewModel
import com.example.anew.ui.fragment.chat.friend.FriendListViewModel
import com.example.anew.ui.fragment.chat.friendrequest.FriendsRequestViewModel
import com.example.anew.ui.fragment.common.otheruser.OtherUserProfileViewModel
import com.example.anew.ui.fragment.home.home.HomeViewModel
import com.example.anew.ui.fragment.home.taskdetail.TaskDetailViewModel
import com.example.anew.ui.fragment.home.profile.ProfileViewModel
import com.example.anew.ui.fragment.notification.NotificationViewModel
import kotlin.getValue

class MyViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepo) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(projectRepo) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(authRepo) as T
            }
            modelClass.isAssignableFrom(NotificationViewModel::class.java) -> {
                NotificationViewModel(notificationRepo) as T
            }
            modelClass.isAssignableFrom(SelectAddMemberViewModel::class.java) -> {
                SelectAddMemberViewModel(friendRepo,messageRepo) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(projectRepo, messageRepo) as T
            }
            modelClass.isAssignableFrom(FriendListViewModel::class.java) ->{
                FriendListViewModel(friendRepo,authRepo) as T
            }
            modelClass.isAssignableFrom(OtherUserProfileViewModel::class.java) ->{
                OtherUserProfileViewModel(authRepo,friendRepo) as T
            }
            modelClass.isAssignableFrom(FriendsRequestViewModel::class.java) ->{
                FriendsRequestViewModel(authRepo,friendRepo) as T
            }
            modelClass.isAssignableFrom(ChatRoomViewModel::class.java) ->{
                ChatRoomViewModel(authRepo,messageRepo,projectRepo,friendRepo) as T
            }
            modelClass.isAssignableFrom(ConversationViewModel::class.java) ->{
                ConversationViewModel(messageRepo) as T
            }
            modelClass.isAssignableFrom(TaskDetailViewModel::class.java) ->{
                TaskDetailViewModel(authRepo,projectRepo,messageRepo) as T
            }
            modelClass.isAssignableFrom(CalendarViewModel::class.java) ->{
                CalendarViewModel(projectRepo) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) ->{
                MainViewModel(notificationRepo) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

     companion object{
         private val authRepo by lazy { AuthRepo() }
         private val messageRepo by lazy { MessageRepo() }
         private val notificationRepo by lazy { NotificationRepo() }
         private val friendRepo by lazy { FriendRepo(notificationRepo) }
         private val projectRepo by lazy { ProjectRepo(notificationRepo,messageRepo) }
     }
}