package edu.pwr.navcomsys.ships.data.dto

import edu.pwr.navcomsys.ships.data.enums.MessageType

data class MessageDto(
    val messageType: MessageType,
    val encodedJson: String
)
