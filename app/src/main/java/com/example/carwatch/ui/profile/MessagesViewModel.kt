package com.example.carwatch.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carwatch.domain.repository.CarWatchRepository
import com.example.carwatch.domain.repository.ChatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val repository: CarWatchRepository
) : ViewModel() {
    private val _chats = MutableStateFlow<List<ChatEntry>>(emptyList())
    val chats: StateFlow<List<ChatEntry>> = _chats.asStateFlow()

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.getChats().collect {
                _chats.value = it
            }
        }
    }
}
