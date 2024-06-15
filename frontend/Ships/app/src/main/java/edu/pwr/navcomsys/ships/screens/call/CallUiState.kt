package edu.pwr.navcomsys.ships.screens.call

data class CallUiState(
    val callStatus: CallStatus = CallStatus.Incoming,
    val caller: String = "Steven Jobs",
    val receiver: String = "",
    val callTime: String = "00:01",
    val isRecording: Boolean = false,
)

enum class CallStatus {
    Incoming, Outgoing, Ongoing
}