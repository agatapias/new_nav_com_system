package edu.pwr.navcomsys.ships.model.repository

import edu.pwr.navcomsys.ships.data.database.ChatMessage
import edu.pwr.navcomsys.ships.model.datasource.local.ChatMessageDataSource
import edu.pwr.navcomsys.ships.model.datasource.local.UserLocalDataSource

class ChatMessageRepository(
    private val chatMessageDataSource: ChatMessageDataSource,
    private val userLocalDataSource: UserLocalDataSource
) {
    suspend fun insertMessage(chatMessage: ChatMessage) {
        chatMessageDataSource.insert(chatMessage)
    }

    suspend fun getConversationWith(username: String): List<ChatMessage> {
        return chatMessageDataSource.getMessagesFromUser(username)
    }

    suspend fun getConversationUsers() : List<String> {
        val username = userLocalDataSource.getUser()?.username
        return username?.let { chatMessageDataSource.getConversationUsers(it) } ?: emptyList()
    }

    suspend fun getAllMessages(): List<ChatMessage> {
        return chatMessageDataSource.getMessages()
    }
}