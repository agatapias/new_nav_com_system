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
import edu.pwr.navcomsys.ships.screens.conversation.ConversationScreen
import edu.pwr.navcomsys.ships.screens.main.MainScreen
import edu.pwr.navcomsys.ships.screens.message.MessageNavigation
import edu.pwr.navcomsys.ships.screens.message.MessageScreen
import edu.pwr.navcomsys.ships.screens.phone.PhoneScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    innerPadding: PaddingValues = PaddingValues(10.dp),
    startDestination: String = Screen.Main.path
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Main.path) {
            MainScreen()
        }

        composable(Screen.Calls.path) {
            PhoneScreen()
        }

        composable(Screen.Messages.path) {
            MessageScreen(MessageNavigation.default(navController))
        }

        composable(Screen.Conversation.path) {
            ConversationScreen()
        }

        composable(Screen.Account.path) {
            AccountScreen()
        }
    }
}