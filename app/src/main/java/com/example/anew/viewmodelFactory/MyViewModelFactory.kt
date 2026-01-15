package com.example.anew.viewmodelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.anew.support.fakeData
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.FriendRepo
import com.example.anew.repo.MessageRepo
import com.example.anew.repo.ProjectRepo
import com.example.anew.ui.activity.login.LoginViewModel
import com.example.anew.ui.fragment.add.AddViewModel
import com.example.anew.ui.fragment.add.SelectAddMemberViewModel
import com.example.anew.ui.fragment.chat.chat_room.ChatRoomViewModel
import com.example.anew.ui.fragment.chat.conversation.ConversationViewModel
import com.example.anew.ui.fragment.chat.friend.FriendListViewModel
import com.example.anew.ui.fragment.chat.friend_request.FriendsRequestViewModel
import com.example.anew.ui.fragment.chat.other_user.OtherUserProfileViewModel
import com.example.anew.ui.fragment.home.HomeViewModel
import com.example.anew.ui.fragment.home.task_detail.TaskDetailViewModel
import com.example.anew.ui.fragment.home.profile.ProfileViewModel

class MyViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(AuthRepo()) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(ProjectRepo()) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(AuthRepo()) as T
            }
            modelClass.isAssignableFrom(SelectAddMemberViewModel::class.java) -> {
                SelectAddMemberViewModel(FriendRepo(), MessageRepo(), fakeData.user!!.uid) as T
            }
            modelClass.isAssignableFrom(AddViewModel::class.java) -> {
                AddViewModel(ProjectRepo(),MessageRepo()) as T
            }
            modelClass.isAssignableFrom(FriendListViewModel::class.java) ->{
                FriendListViewModel(FriendRepo(), AuthRepo()) as T
            }
            modelClass.isAssignableFrom(OtherUserProfileViewModel::class.java) ->{
                OtherUserProfileViewModel(AuthRepo(), FriendRepo()) as T
            }
            modelClass.isAssignableFrom(FriendsRequestViewModel::class.java) ->{
                FriendsRequestViewModel(AuthRepo(), FriendRepo()) as T
            }
            modelClass.isAssignableFrom(ChatRoomViewModel::class.java) ->{
                ChatRoomViewModel(AuthRepo(), MessageRepo(), ProjectRepo(),FriendRepo()) as T
            }
            modelClass.isAssignableFrom(ConversationViewModel::class.java) ->{
                ConversationViewModel(MessageRepo()) as T
            }
            modelClass.isAssignableFrom(TaskDetailViewModel::class.java) ->{
                TaskDetailViewModel(AuthRepo(),ProjectRepo(),MessageRepo()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}