package edu.pwr.navcomsys.ships.data.dto

data class ChatMessageDto(
    val fromUsername: String,
    val toUsername: String,
    val fromAddress: String,
    val toAddress: String,
    val message: String,
    val createdDate: String,
    val createdTime: String
)
