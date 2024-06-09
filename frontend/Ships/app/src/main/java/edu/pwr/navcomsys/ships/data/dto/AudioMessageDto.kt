package edu.pwr.navcomsys.ships.data.dto

data class AudioMessageDto(
    val fromAddress: String,
    val toAddress: String,
    val type: AudioMessageType,
    val message: ByteArray? = null
)

enum class AudioMessageType {
    Accept, End, Message
}
