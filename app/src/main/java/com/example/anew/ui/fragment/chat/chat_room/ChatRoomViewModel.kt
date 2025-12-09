package com.example.anew.ui.fragment.chat.chat_room

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Message
import com.example.anew.model.UiState
import com.example.anew.repo.MessageRepo
import com.example.anew.support.convertUriToCloudinaryUrl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ChatRoomViewModel(private val messageRepo: MessageRepo): ViewModel() {
    private val _messageState = MutableStateFlow<UiState<List<Message>>>(UiState.Loading)
    val messageState: StateFlow<UiState<List<Message>>> = _messageState.asStateFlow()

    fun getMessages(roomId: String){
        viewModelScope.launch {
            _messageState.value= UiState.Loading
            messageRepo.getMessages(roomId).catch {
                _messageState.value = UiState.Error(it.message.toString())
            }.collect{
                _messageState.value = UiState.Success(it)
            }
        }
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