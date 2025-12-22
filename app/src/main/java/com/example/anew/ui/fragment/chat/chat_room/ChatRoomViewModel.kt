package com.example.anew.ui.fragment.chat.chat_room

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Message
import com.example.anew.model.MessageItem
import com.example.anew.model.UiState
import com.example.anew.model.User
import com.example.anew.repo.AuthRepo
import com.example.anew.repo.MessageRepo
import com.example.anew.support.DataTranfer
import com.example.anew.support.convertUriToCloudinaryUrl
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChatRoomViewModel(
    private val messageRepo: MessageRepo,
    private val authRepo: AuthRepo
): ViewModel() {
    private val _messageState = MutableStateFlow<UiState<List<MessageItem>>>(UiState.Loading)
    val messageState: StateFlow<UiState<List<MessageItem>>> = _messageState.asStateFlow()


    //NOTEEEE HÔM SAU CHECK
    fun getMessages(roomId: String) {
        viewModelScope.launch {
            _messageState.value = UiState.Loading
            messageRepo.getMessages(roomId).catch {
                _messageState.value = UiState.Error(it.message.toString())
            }.collect { listMessage ->
                val loadUserId = listMessage.map { it.senderId }.distinct()
                    .filter { !DataTranfer.userCache.containsKey(it) }

                loadUserId.map { it -> async { getUserData(it)} }.awaitAll()
                _messageState.value = UiState.Success(listMessage.map { message ->
                    MessageItem(
                        message.messageId,
                        message.senderId,
                        DataTranfer.userCache[message.senderId]!!.name,
                        DataTranfer.userCache[message.senderId]!!.photoUrl,
                        message.message,
                        message.imageUrlList,
                        message.time
                    )
                })
            }
        }
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
}