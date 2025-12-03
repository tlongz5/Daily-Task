package com.example.anew.ui.fragment.chat.conversation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anew.model.Conversation
import com.example.anew.model.UiState
import com.example.anew.repo.MessageRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ConversationViewModel(private val messageRepo: MessageRepo): ViewModel() {
    private val _conservationState = MutableStateFlow<UiState<List<Conversation>>>(UiState.Loading)
    val conservationState: StateFlow<UiState<List<Conversation>>> = _conservationState.asStateFlow()

    fun getConversation(userId: String,chatType: String) {
        viewModelScope.launch {
            _conservationState.value = UiState.Loading
            messageRepo.getConversation(userId,chatType).catch { e->
                _conservationState.value = UiState.Error(e.message?:"Check internet connection")
            }.collect {
                _conservationState.value = UiState.Success(it)
            }
        }
    }

}
