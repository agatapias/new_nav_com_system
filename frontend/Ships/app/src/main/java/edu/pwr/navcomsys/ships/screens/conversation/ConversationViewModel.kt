package edu.pwr.navcomsys.ships.screens.conversation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import edu.pwr.navcomsys.ships.data.database.ChatMessage
import edu.pwr.navcomsys.ships.data.dto.ChatMessageDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.ChatMessageRepository
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ConversationViewModel(
    val peerRepository: PeerRepository,
    val userInfoRepository: UserInfoRepository,
    val chatMessageRepository: ChatMessageRepository,
    val messageListener: MessageListener
) : ViewModel() {
    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState = _uiState.asStateFlow()

    private var conversationUsername: String? = null

    init {
        CoroutineScope(Dispatchers.IO).launch {
            messageListener.chatMessages.filter {
                it?.fromUsername == conversationUsername
            }.collect {
                addMessage(it!!)
            }
        }
    }

    fun setUsername(name: String?) {
        if (conversationUsername == null || conversationUsername != name) {
            conversationUsername = name
        }
    }

    fun onInputChange(value: String) {
        _uiState.update {
            it.copy(input = value)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun onSendMessage(value: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val host = conversationUsername?.let { peerRepository.getHostByUsername(it) }
            val user = userInfoRepository.getUser()
            val ownIpAddress = peerRepository.getOwnIpInfo().ipAddress
            val createdDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val createdTime = LocalDate.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

            if (conversationUsername == null || host == null) {
                return@launch
            }

            val chatMessage = ChatMessageDto(
                user?.username ?: "",
                conversationUsername!!,
                ownIpAddress,
                host,
                value,
                createdDate,
                createdTime
            )

            val messageDtoJson = peerRepository.convertToJson(chatMessage, MessageType.CHAT)
            peerRepository.sendMessage(host, messageDtoJson)

            val chatMessageEntity = ChatMessage(
                0,
                user?.username ?: "",
                conversationUsername!!,
                value,
                createdDate,
                createdTime
            )
            chatMessageRepository.insertMessage(chatMessageEntity)

            addMessage(chatMessage)
        }
    }

    private fun addMessage(chatMessage: ChatMessageDto) {
        val isOwn = chatMessage.fromUsername != conversationUsername
        val message = ConversationMessageData(chatMessage.message, chatMessage.createdTime, isOwn)
        val newMessages = _uiState.value.messages + message
        _uiState.update { it.copy(messages = newMessages) }
    }
}