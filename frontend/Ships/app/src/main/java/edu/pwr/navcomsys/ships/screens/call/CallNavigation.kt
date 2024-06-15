package edu.pwr.navcomsys.ships.screens.call

import android.util.Log
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.getArguments

interface CallNavigation {
    val ip: String?
    val type: CallStatus
    val toUsername: String?
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

            override val toUsername: String?
                get() = backStackEntry.getArguments()[2]

            override fun goBack() {
                navController.popBackStack()
            }
        }
    }
}

private fun getType(backStackEntry: NavBackStackEntry): CallStatus {
    val value = backStackEntry.getArguments()[1]
    Log.d("Call", "value: $value")
    return if (value == "O")
        CallStatus.Outgoing
    else
        CallStatus.Incoming
}