package edu.pwr.navcomsys.ships.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer

@Composable
fun DashboardScreen(
    navigation: DashboardNavigation
) {
    DashboardScreenContent(navigation)
}

@Composable
private fun DashboardScreenContent(
    navigation: DashboardNavigation
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.space40),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_globe),
                contentDescription = null,
            )
        }
        Dimensions.space20.HeightSpacer()
        ShipButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Mapa statków",
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            type = ShipButtonType.Primary
        ) {
            navigation.openMap()
        }
        Dimensions.space20.HeightSpacer()
        ShipButton(
            modifier = Modifier.fillMaxWidth(),
            text = "Statki w pobliżu",
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            type = ShipButtonType.Secondary
        ) {
            navigation.openShipList()
        }
    }
}