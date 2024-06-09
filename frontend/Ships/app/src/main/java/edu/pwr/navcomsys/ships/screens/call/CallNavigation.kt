package edu.pwr.navcomsys.ships.screens.call

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.getArguments

interface CallNavigation {
    val ip: String?
    val type: CallStatus
    fun goBack()

    companion object {
        fun default(
            navController: NavHostController,
            backStackEntry: NavBackStackEntry
        ) = object : CallNavigation {
            override val ip: String?
                get() = backStackEntry.getArguments().firstOrNull()

            override val type: CallStatus
                get() = getType(backStackEntry)

            override fun goBack() {
                navController.popBackStack()
            }
        }
    }
}

private fun getType(backStackEntry: NavBackStackEntry): CallStatus {
    val value = backStackEntry.getArguments()[1]
    if (value == "O")
        return CallStatus.Outgoing
    else
        return CallStatus.Incoming
}