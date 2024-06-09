package edu.pwr.navcomsys.ships.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_message")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fromUsername: String,
    val toUsername: String,
    val message: String,
    val createdDate: String
)
