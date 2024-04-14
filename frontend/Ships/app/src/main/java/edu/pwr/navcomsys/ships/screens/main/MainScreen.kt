package edu.pwr.navcomsys.ships.screens.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import edu.pwr.navcomsys.ships.R

@Composable
fun MainScreen() {
    Text(text = "Hello ships!")
}

@Composable
private fun MainScreenContent() {
    Text(text = "Hello ships!")
    ShipsListFloatingButton(onClick = {})
}

@Composable
fun ShipsListFloatingButton(onClick: () -> Unit) {
    SmallFloatingActionButton(
        onClick = { onClick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.surface,
        shape = CircleShape,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_list),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface),
            contentDescription ="Action button for list of ships.")
    }
}