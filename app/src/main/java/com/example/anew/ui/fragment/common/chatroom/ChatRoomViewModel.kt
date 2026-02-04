package com.example.anew.ui.fragment.common.chatroom

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.ConversationInfo
import com.example.anew.model.Message
import com.example.anew.model.MessageItem
import com.example.anew.model.Team
import com.example.anew.model.UiState
import com.example.anew.model.User
import com.example.anew.data.repo.AuthRepo
import com.example.anew.data.repo.FriendRepo
import com.example.anew.data.repo.MessageRepo
import com.example.anew.data.repo.ProjectRepo
import com.example.anew.data.local.DataTranfer
import com.example.anew.data.local.MyHelper
import com.example.anew.utils.convertUriToCloudinaryUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChatRoomViewModel(
    private val authRepo: AuthRepo,
    private val messageRepo: MessageRepo,
    private val projectRepo: ProjectRepo,
    private val friendRepo: FriendRepo
): ViewModel() {
    private val _messageState = MutableStateFlow<UiState<List<MessageItem>>>(UiState.Loading)
    val messageState: StateFlow<UiState<List<MessageItem>>> = _messageState.asStateFlow()

    private val _conversationInfoState = MutableLiveData<ConversationInfo>()
    val conversationInfoState: MutableLiveData<ConversationInfo> = _conversationInfoState

    private val _projectState = MutableLiveData<Team>()
    val projectState: MutableLiveData<Team> = _projectState

    private val _friendListState = MutableLiveData<List<User>>()
    val friendListState: MutableLiveData<List<User>> = _friendListState

    private val _checkFriendState = MutableLiveData<Int>()
    val checkFriendState: MutableLiveData<Int> = _checkFriendState

    private val allMessage = mutableListOf<MessageItem>()
    private var isContinue = true

    fun getMessages(roomId: String) {
        viewModelScope.launch {
            _messageState.value = UiState.Loading
            messageRepo.getMessages(roomId).catch {
                _messageState.value = UiState.Error(it.message.toString())
            }.collect { listMessage ->
                val newList =listMessage.filter { newMsg->
                    allMessage.none { realMsg -> realMsg.messageId == newMsg.messageId }
                }
                addToList(newList,true)
            }
        }
    }

    fun loadMoreMessages(roomId: String, lastMessageId: String) {
        viewModelScope.launch {
            try {
                if(!isContinue) return@launch
                val olderMessage = messageRepo.loadMoreMessages(roomId, lastMessageId)
                addToList(olderMessage,false)
            }catch (e: Exception){
                _messageState.value = UiState.Error(e.message.toString())
            }
        }
    }

    private suspend fun addToList(newList: List<Message>, isNew: Boolean) {
        if(newList.isEmpty()) return

        val loadUserId = newList.map { it.senderId }.distinct()
            .filter { !DataTranfer.userCache.containsKey(it) }
        loadData(loadUserId)
        val newOrOlderMessageItem = newList.map { message ->
            MessageItem(
                message.messageId,
                message.senderId,
                DataTranfer.userCache[message.senderId]!!.name,
                DataTranfer.userCache[message.senderId]!!.photoUrl,
                message.message,
                message.imageUrlList,
                message.time
            )
        }
        if (isNew) allMessage.addAll(newOrOlderMessageItem)
        else allMessage.addAll(0, newOrOlderMessageItem)
        _messageState.value = UiState.Success(if (allMessage.isEmpty()) emptyList() else allMessage.toList())

        if(!isNew && newList.size<50) isContinue=false
    }

    suspend fun loadData(listUserId: List<String>) = withContext(Dispatchers.IO){
        listUserId.map { it -> async { getUserData(it)} }.awaitAll()
    }

    suspend fun getUserData(userId: String){
        if(DataTranfer.userCache.containsKey(userId)) return
        val user = authRepo.getUserDataFromUid(userId)
        DataTranfer.userCache[userId] = user
    }

    fun pushMessage(chatId: String,
                    chatName:String,

                    senderId: String,
                    nameSender: String,
                    avatar: String,

                    receiverId: String,
                    receiverName: String,
                    receiverAvatar: String,

                    lastMessage: String,
                    imageUriList: List<Uri>,
                    chatType: String
    ){
        viewModelScope.launch {
            messageRepo.pushMessage(chatId,
                chatName,

                senderId,
                nameSender,
                avatar,

                receiverId,
                receiverName,
                receiverAvatar,

                lastMessage,
                convertUriToCloudinaryUrl(imageUriList),
                chatType)
        }
    }

    suspend fun convertUriToCloudinaryUrl(imgUriList: List<Uri>): List<String> {
        return imgUriList.map { imgUri -> convertUriToCloudinaryUrl(imgUri) }
    }

    // get Data to load for AdminId
    fun getConversationInfo(chatId: String){
        viewModelScope.launch { _conversationInfoState.value = messageRepo.getConversationInfo(chatId) }
    }

    fun getProject(chatId: String) {
        viewModelScope.launch { _projectState.value = projectRepo.getProjectFromId(chatId) }
    }

    fun editNameProject(chatId: String, groupName: String) {
        viewModelScope.launch { projectRepo.editNameProject(chatId,groupName) }
    }

    fun changeAvatar(chatType: String,chatId: String, uri: Uri) {
        viewModelScope.launch {
            messageRepo.changeAvatar(chatType,chatId,
                convertUriToCloudinaryUrl(uri),
                conversationInfoState.value.users.keys.toList())
        }
    }

    fun changeAvatarProject(chatId: String, uri: Uri){
        viewModelScope.launch {
            projectRepo.changeAvatarProject(chatId,
                convertUriToCloudinaryUrl(uri))
        }
    }

    fun addMember(chatType: String,chatId: String, users: List<String>) {
        viewModelScope.launch {
            messageRepo.addMember(chatType,chatId, users)
        }
    }

    fun changeLeader(chatId: String, userId: String) {
        viewModelScope.launch {
            messageRepo.changeLeader(chatId, userId)
        }
    }

    suspend fun removeMemberFromGroup(chatType: String,chatId: String,userId: String) {
        messageRepo.removeMemberFromGroup(chatType ,chatId, userId)
    }

    fun editGroupName(chatType: String,chatId: String, groupName: String) {
        viewModelScope.launch {
            messageRepo.editGroupName(chatType,chatId, groupName,
                conversationInfoState.value.users.keys.toList())
        }
    }

    fun updateProgress(isCompleted: Boolean) {
        viewModelScope.launch {
            _projectState.value = projectRepo.updateProgress(projectState.value!!.projectId, isCompleted, MyHelper.user!!.uid)
        }
    }

    fun updateDataAfterAddOrDelete(isAdd: Boolean, userIdList: List<String>){
        viewModelScope.launch {
            _projectState.value = projectRepo.updateDataAfterAddOrDelete(projectState.value!!.projectId, isAdd, userIdList)
        }
    }

    fun getFriendListData(userId:String){
        viewModelScope.launch {
            val userListId = friendRepo.getFriendListUser(userId)
            loadData(userListId)
            _friendListState.value = userListId.map { DataTranfer.userCache[it]!! }
        }
    }

    fun requestFriend(userId: String) {
        viewModelScope.launch {
            friendRepo.requestFriend(MyHelper.user!!, userId)
            _checkFriendState.value = friendRepo.checkFriend(MyHelper.user!!.uid, userId)
        }
    }

    fun checkFriend(userId: String) {
        viewModelScope.launch {
            _checkFriendState.value = friendRepo.checkFriend(MyHelper.user!!.uid, userId)
        }
    }

    fun unFriend(friendId: String) = viewModelScope.launch {
        friendRepo.unFriend(MyHelper.user!!.uid, friendId)
        _checkFriendState.value = -1
    }

}