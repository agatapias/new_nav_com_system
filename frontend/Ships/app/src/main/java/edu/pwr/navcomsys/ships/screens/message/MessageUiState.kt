package edu.pwr.navcomsys.ships.screens.message

import androidx.annotation.DrawableRes
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.data.dto.ChatMessageDto

data class MessageUiState(
    val messages: List<MessageData> = listOf(MessageData.mock(), MessageData.mock(), MessageData.mock()),
    val messageBuffer: List<ChatMessageDto> = emptyList(),
    val isLoading: Boolean = false
)

data class MessageData(
    val conversationId: String,
    @DrawableRes val image: Int,
    val name: String,
    val date: String,
    val time: String,
    val lastMessage: String
) {
    companion object {
        fun mock() = MessageData(
            conversationId = "1",
            image = R.drawable.ic_ship,
            name = "Rosa",
            date = "12.03.2024",
            time = "17:45",
            lastMessage = "Ty: Cześć!"
        )
    }
}