package edu.pwr.navcomsys.ships.data.dto

data class ChatMessageDto(
    val fromUsername: String,
    val fromAddress: String,
    val toAddress: String,
    val message: String
)
