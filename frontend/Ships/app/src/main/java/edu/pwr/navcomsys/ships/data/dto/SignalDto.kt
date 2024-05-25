package edu.pwr.navcomsys.ships.data.dto

import edu.pwr.navcomsys.ships.data.enums.SignalType

data class SignalDto(
    val username: String,
    val type: SignalType,
    val data: String,
    val toUsername: String?
)