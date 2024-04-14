@file:OptIn(ExperimentalMaterial3Api::class)

package edu.pwr.navcomsys.ships.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.pwr.navcomsys.ships.ui.theme.Dimensions

private val SCREENS_WITHOUT_BOTTOM_BAR = listOf<String>()

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    startDestination: String = Screen.Main.path,
    showMessageDot: Boolean
) {
    val bottomNavItems: List<Screen> = listOf(
        Screen.Main,
        Screen.Calls,
        Screen.Messages,
        Screen.Account,
    )

    // Subscribe to navBackStackEntry, required to get current route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Log.d("route", currentDestination?.route ?: "no route")

    val bottomBarState = when (currentDestination?.route) {
        in SCREENS_WITHOUT_BOTTOM_BAR -> false
        else -> true
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = bottomBarState,
                enter = slideInVertically(initialOffsetY = { s -> s } ),
                exit = slideOutVertically(targetOffsetY = { t -> t } )
            ) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        val iconSize = if (screen.path == Screen.Messages.path) 40.dp else Dimensions.space32
                        NavigationBarItem(
                            modifier = Modifier.testTag(screen.tag),
                            icon = {
                                if (screen.icon != null && screen.description != null)
                                    RedDotWrapper(isVisible = screen.path == Screen.Messages.path && showMessageDot) {
                                        Image(
                                            modifier = Modifier.size(iconSize),
                                            painter = painterResource(id = screen.icon),
                                            contentDescription = stringResource(id = screen.description),
                                            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.onBackground)
                                        )
                                    }
                            },
                            label = {
                                screen.label?.let {
                                    Text(
                                        text = stringResource(id = it),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onBackground
                                    )
                                }
                            },
                            alwaysShowLabel = true,
                            selected = currentDestination?.hierarchy?.any { it.route == screen.path } == true,
                            onClick = {
                                navController.navigate(screen.path) { launchSingleTop = true }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            innerPadding = innerPadding,
            startDestination = startDestination
        )
    }
}

@Composable
private fun RedDotWrapper(
    isVisible: Boolean,
    content: @Composable () -> Unit
) {
    val red = MaterialTheme.colorScheme.error
    if (isVisible) {
        Box {
            Box(modifier = Modifier.align(Alignment.Center)) {
                content()
            }
            Canvas(
                modifier = Modifier
                    .size(4.dp)
                    .align(Alignment.TopEnd),
                onDraw = {
                    drawCircle(color = red)
                }
            )
        }
    } else {
        content()
    }
}