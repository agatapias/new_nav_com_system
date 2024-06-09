package edu.pwr.navcomsys.ships.screens.shiplist

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen
import edu.pwr.navcomsys.ships.ui.navigation.appendArguments

interface ShipListNavigation {
    fun openConversation(name: String)

    companion object {
        fun default(
            navController: NavHostController
        ) = object : ShipListNavigation {

            override fun openConversation(name: String) {
                navController.navigate(Screen.Conversation.path.appendArguments(name))
            }

        }
    }
}