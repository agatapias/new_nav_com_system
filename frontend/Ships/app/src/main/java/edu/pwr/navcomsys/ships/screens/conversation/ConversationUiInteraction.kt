package edu.pwr.navcomsys.ships.screens.conversation

import android.os.Build
import androidx.annotation.RequiresApi

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

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSendMessage(value: String) {
                viewModel.onSendMessage(value)
            }

        }
    }
}