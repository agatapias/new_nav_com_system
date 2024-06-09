package edu.pwr.navcomsys.ships.screens.phone

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen
import edu.pwr.navcomsys.ships.ui.navigation.appendArguments

interface PhoneNavigation {
    fun openCall(ip: String)

    companion object {
        fun default(
            navController: NavHostController
        ) = object : PhoneNavigation {
            override fun openCall(ip: String) {
                navController.navigate(Screen.Call.path.appendArguments(ip))
            }

        }
    }
}