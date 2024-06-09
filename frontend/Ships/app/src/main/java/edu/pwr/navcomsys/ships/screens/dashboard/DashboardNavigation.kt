package edu.pwr.navcomsys.ships.screens.dashboard

import androidx.navigation.NavHostController
import edu.pwr.navcomsys.ships.ui.navigation.Screen

interface DashboardNavigation {
    fun openMap()
    fun openShipList()

    companion object {
        fun default(
            navController: NavHostController
        ) = object : DashboardNavigation {
            override fun openMap() {
                navController.navigate(Screen.Main.path)
            }

            override fun openShipList() {
                navController.navigate(Screen.ShipList.path)
            }

        }
    }
}