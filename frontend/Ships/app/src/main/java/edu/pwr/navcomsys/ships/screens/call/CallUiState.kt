package edu.pwr.navcomsys.ships.screens.call

data class CallUiState(
    val callStatus: CallStatus = CallStatus.Incoming,
    val caller: String = "Steven Jobs",
    val receiver: String = "",
    val isOngoing: Boolean = false,
    val callTime: String = "00:01"
)

enum class CallStatus {
    Incoming, Outgoing, Ongoing
}