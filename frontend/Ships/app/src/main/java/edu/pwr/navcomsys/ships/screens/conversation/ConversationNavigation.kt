package edu.pwr.navcomsys.ships.screens.conversation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.getArguments

interface ConversationNavigation {
    val name: String?

    companion object {
        fun default(
            navController: NavHostController,
            backStackEntry: NavBackStackEntry
        ) = object : ConversationNavigation {
            override val name: String?
                get() = backStackEntry.getArguments().firstOrNull()

        }
    }
}