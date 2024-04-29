package edu.pwr.navcomsys.ships.screens.conversation

data class ConversationUiState(
    val input: String = "",
    val messages: List<ConversationMessageData> = listOf(ConversationMessageData.mock2(), ConversationMessageData.mock1()),
    val accountData: AccountData = AccountData(),
    val isLoading: Boolean = false,
)

data class ConversationMessageData(
    val message: String,
    val read: String,
    val isYourMessage: Boolean
) {
    companion object {
        fun mock1() = ConversationMessageData(
            message = "Ahoj marynarzu!\n" +
                    "Dobrze Cię widzieć!\n" +
                    "Od 15 dni nikt się nie odzywał, wichura zmiotła wszystkich...",
            read = "whatever",
            isYourMessage = false
        )

        fun mock2() = ConversationMessageData(
            message = "Przepraszam?",
            read = "13:10",
            isYourMessage = true
        )
    }
}

data class AccountData(
    val shipName: String = "",
    val personName: String = ""
)