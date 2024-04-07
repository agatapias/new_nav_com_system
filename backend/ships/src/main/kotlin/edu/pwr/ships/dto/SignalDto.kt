package edu.pwr.ships.dto

data class SignalDto(
    val username: String,
    val type: SignalType,
    val data: String,
    val toUsername: String?
)
