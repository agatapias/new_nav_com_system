package edu.pwr.navcomsys.ships.screens.call

interface CallUiInteraction {
    fun onRecordPress()
    fun acceptCall()
    fun endCall()

    companion object {
        fun default(
            viewModel: CallViewModel
        ) = object : CallUiInteraction {
            override fun onRecordPress() {
                viewModel.onRecordPress()
            }

            override fun acceptCall() {
                viewModel.acceptConversation()
            }

            override fun endCall() {
                viewModel.endConversation()
            }

        }

        fun empty() = object : CallUiInteraction {
            override fun onRecordPress() {}

            override fun acceptCall() {}

            override fun endCall() {}

        }
    }
}