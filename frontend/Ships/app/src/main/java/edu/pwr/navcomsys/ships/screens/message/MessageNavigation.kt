package edu.pwr.navcomsys.ships.screens.message

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen

interface MessageNavigation {
    fun onNewMessageClick()
    fun onConversationClick(id: String)

    companion object {
        fun default(
            navController: NavHostController
        ) = object : MessageNavigation {

            override fun onNewMessageClick() {
                TODO("Not yet implemented")
            }

            override fun onConversationClick(id: String) {
                navController.navigate(Screen.Conversation.path)
            }
        }
    }
}
