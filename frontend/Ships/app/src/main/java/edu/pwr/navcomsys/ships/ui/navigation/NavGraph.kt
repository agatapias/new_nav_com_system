package edu.pwr.navcomsys.ships.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.pwr.navcomsys.ships.screens.account.AccountScreen
import edu.pwr.navcomsys.ships.screens.call.CallNavigation
import edu.pwr.navcomsys.ships.screens.call.CallScreen
import edu.pwr.navcomsys.ships.screens.conversation.ConversationNavigation
import edu.pwr.navcomsys.ships.screens.conversation.ConversationScreen
import edu.pwr.navcomsys.ships.screens.dashboard.DashboardNavigation
import edu.pwr.navcomsys.ships.screens.dashboard.DashboardScreen
import edu.pwr.navcomsys.ships.screens.main.MainNavigation
import edu.pwr.navcomsys.ships.screens.main.MainScreen
import edu.pwr.navcomsys.ships.screens.message.MessageNavigation
import edu.pwr.navcomsys.ships.screens.message.MessageScreen
import edu.pwr.navcomsys.ships.screens.phone.PhoneNavigation
import edu.pwr.navcomsys.ships.screens.phone.PhoneScreen
import edu.pwr.navcomsys.ships.screens.shiplist.ShipListNavigation
import edu.pwr.navcomsys.ships.screens.shiplist.ShipListScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    startDestination: String = Screen.Dashboard.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Main.path) {
            MainScreen(MainNavigation.default(navController))
        }

        composable(Screen.Calls.path) {
            PhoneScreen(navigation = PhoneNavigation.default(navController))
        }

        composable(Screen.Messages.path) {
            MessageScreen(MessageNavigation.default(navController))
        }

        composable(Screen.Conversation.path) { backStackEntry ->
            ConversationScreen(navigation = ConversationNavigation.default(
                navController = navController,
                backStackEntry = backStackEntry
            ))
        }

        composable(Screen.Account.path) {
            AccountScreen()
        }

        composable(Screen.ShipList.path) {
            ShipListScreen(navigation = ShipListNavigation.default(navController))
        }

        composable(Screen.Dashboard.path) {
            DashboardScreen(navigation = DashboardNavigation.default(navController))
        }

        composable(Screen.Call.path) { backStackEntry ->
            CallScreen(navigation = CallNavigation.default(
                navController = navController,
                backStackEntry = backStackEntry
            ))
        }
    }
}