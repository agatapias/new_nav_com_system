package edu.pwr.navcomsys.ships.screens.main

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen

interface MainNavigation {
    fun openShipList()

    companion object {
        fun default(
            navController: NavHostController
        ) = object : MainNavigation {
            override fun openShipList() {
                navController.navigate(Screen.ShipList.path)
            }

        }
    }
}