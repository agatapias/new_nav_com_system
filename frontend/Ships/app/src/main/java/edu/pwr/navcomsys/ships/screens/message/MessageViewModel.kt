package edu.pwr.navcomsys.ships.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.model.repository.ChatMessageRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    val messageListener: MessageListener,
    val chatMessageRepository: ChatMessageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MessageUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            messageListener.chatMessages
                .filter { it != null }
                .collect{
                    val buffer = _uiState.value.messageBuffer
                    val newBuffer = buffer + it!!
                    _uiState.update { it.copy(messageBuffer = newBuffer) }
                }

            val chatMessageHistory = chatMessageRepository.getAllMessages()

            _uiState.update {  }

            _uiState.map { it.messageBuffer }.collect {

            }
        }
    }
}