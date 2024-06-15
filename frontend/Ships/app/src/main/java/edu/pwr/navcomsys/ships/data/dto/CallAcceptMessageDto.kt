package edu.pwr.navcomsys.ships.data.dto

data class CallAcceptMessageDto(
    val fromAddress: String,
    val toAddress: String,
    val fromUsername: String = "",
    val toUsername: String = ""
)
