package edu.pwr.navcomsys.ships.screens.call

data class CallUiState(
    val callStatus: CallStatus = CallStatus.Incoming,
    val caller: String = "andrew",
    val receiver: String = "statek1",
    val callTime: String = "00:01",
    val isRecording: Boolean = false,
)

enum class CallStatus {
    Incoming, Outgoing, Ongoing
}