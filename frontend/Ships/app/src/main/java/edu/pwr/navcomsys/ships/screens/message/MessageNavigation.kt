package edu.pwr.navcomsys.ships.screens.message

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen
import edu.pwr.navcomsys.ships.ui.navigation.appendArguments

interface MessageNavigation {
    fun onNewMessageClick()
    fun onConversationClick(name: String)

    companion object {
        fun default(
            navController: NavHostController
        ) = object : MessageNavigation {

            override fun onNewMessageClick() {
                TODO("Not yet implemented")
            }

            override fun onConversationClick(name: String) {
                navController.navigate(Screen.Conversation.path.appendArguments(name))
            }
        }
    }
}
