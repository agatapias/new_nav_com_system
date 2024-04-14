package edu.pwr.navcomsys.ships.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import edu.pwr.navcomsys.ships.ui.theme.Dimensions

@Composable
fun ShipDialog(
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(
                        horizontal = Dimensions.space22,
                        vertical = Dimensions.space26
                    )
            ) {
                content()
            }
        }
    }
}