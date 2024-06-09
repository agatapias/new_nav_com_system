package edu.pwr.navcomsys.ships.screens.phone

data class PhoneUiState(
    val selectedTab: PhoneTab = PhoneTab.Outgoing,
    val calls: List<CallData> = listOf(),
    val isLoading: Boolean = false
)

enum class PhoneTab {
    Incoming, Outgoing
}

data class CallData(
    val name: String,
    val date: String,
    val time: String,
    val ip: String
) {
    companion object {
        fun mock() = CallData(
            name = "Rosa",
            date = "12.03.2024",
            time = "17:45",
            ip = "+48 111 222 333"
        )
    }
}