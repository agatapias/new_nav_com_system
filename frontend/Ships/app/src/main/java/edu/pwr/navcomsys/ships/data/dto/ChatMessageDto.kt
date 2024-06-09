package edu.pwr.navcomsys.ships.data.dto

import java.util.Date

data class ChatMessageDto(
    val fromUsername: String,
    val fromAddress: String,
    val toAddress: String,
    val message: String,
    val createdDate: Date
)
