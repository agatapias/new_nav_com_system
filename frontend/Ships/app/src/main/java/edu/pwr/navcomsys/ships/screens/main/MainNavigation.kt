package edu.pwr.navcomsys.ships.screens.main

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen
import edu.pwr.navcomsys.ships.ui.navigation.appendArguments

interface MainNavigation {
    fun openShipList()
    fun openConversation(name: String)

    companion object {
        fun default(
            navController: NavHostController
        ) = object : MainNavigation {
            override fun openShipList() {
                navController.navigate(Screen.ShipList.path)
            }

            override fun openConversation(name: String) {
                navController.navigate(Screen.Conversation.path.appendArguments(name))
            }

        }
    }
}