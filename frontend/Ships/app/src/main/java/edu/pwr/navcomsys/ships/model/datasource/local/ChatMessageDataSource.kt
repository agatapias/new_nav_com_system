package edu.pwr.navcomsys.ships.model.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import edu.pwr.navcomsys.ships.data.database.ChatMessage

@Dao
interface ChatMessageDataSource {
    @Insert
    suspend fun insert(user: ChatMessage)

    @Query("SELECT * FROM chat_message ORDER BY createdDate, createdTime DESC")
    suspend fun getMessages(): List<ChatMessage>

    @Query("SELECT * FROM chat_message WHERE fromUsername = :username OR toUsername = :username ORDER BY createdDate, createdTime DESC")
    suspend fun getMessagesFromUser(username: String): List<ChatMessage>

    @Query(
        "SELECT DISTINCT toUsername FROM chat_message WHERE fromUsername = :username " +
                "UNION " +
                "SELECT DISTINCT fromUsername FROM chat_message WHERE toUsername = :username"
    )
    suspend fun getConversationUsers(username: String) : List<String>
}