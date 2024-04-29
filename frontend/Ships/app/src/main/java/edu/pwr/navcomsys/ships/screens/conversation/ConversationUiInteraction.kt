package edu.pwr.navcomsys.ships.screens.conversation

interface ConversationUiInteraction {
    fun onValueChange(value: String)
    fun onSendMessage(value: String)

    companion object {
        fun default(
            viewModel: ConversationViewModel
        ) = object : ConversationUiInteraction {
            override fun onValueChange(value: String) {
                viewModel.onInputChange(value)
            }

            override fun onSendMessage(value: String) {
                viewModel.onSendMessage(value)
            }

        }
    }
}