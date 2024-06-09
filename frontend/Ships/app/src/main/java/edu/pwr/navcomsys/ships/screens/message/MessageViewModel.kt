package edu.pwr.navcomsys.ships.screens.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.model.repository.ChatMessageRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    val messageListener: MessageListener,
    val chatMessageRepository: ChatMessageRepository,
    val userInfoRepository: UserInfoRepository
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
                    _uiState.update { state -> state.copy(messageBuffer = newBuffer) }
                }

            val user = userInfoRepository.getUser()

            val chatMessageHistory = chatMessageRepository.getAllMessages()
                .groupBy {
                    if (it.toUsername == user?.username) {
                        it.fromUsername
                    } else if (it.fromUsername == user?.username) {
                        it.toUsername
                    } else {
                        it.fromUsername
                    }
                }.map { it.value.maxBy { value -> value.createdDate } }
                .map {
                    val name = if (it.fromUsername == user?.username) it.toUsername else it.fromUsername
                    MessageData(R.drawable.ic_ship, name, it.createdDate, it.createdTime, it.message)
                }

            _uiState.map { it.messageBuffer }.collect {
                val newMessages = it.groupBy { el -> el.fromUsername }
                    .map { el -> el.value.maxBy { value -> value.createdDate } }
                    .map { el -> MessageData(R.drawable.ic_ship, el.fromUsername, el.createdDate, el.createdTime, el.message) }
                val filteredOldMessages = chatMessageHistory.filter { el -> el.name !in newMessages.map { m -> m.name } }
                val messages = filteredOldMessages + newMessages
                _uiState.update { s -> s.copy(messages = messages) }
            }

        }
    }
}