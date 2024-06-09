package edu.pwr.navcomsys.ships.data.dto

data class AudioMessageDto(
    val fromUsername: String,
    val fromAddress: String,
    val toAddress: String,
    val message: ByteArray
)
