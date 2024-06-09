package edu.pwr.navcomsys.ships.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.extenstion.withSuffix
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.Typography

data class ShipData(
    val userName: String,
    val shipName: String,
    val location: ShipLocation,
    val description: String
) {
    companion object {
        fun mock() = ShipData(
            userName = "Miś",
            shipName = "Rosa",
            location = ShipLocation(lat = 12.44, lng = 10.11),
            description = "Best ship on earth"
        )

        fun LocationDto.toShipData() = ShipData(
            userName = username,
            shipName = shipName,
            location = ShipLocation(lat = xCoordinate, lng = yCoordinate),
            description = description
        )
    }
}

data class ShipLocation(
    val lat: Double,
    val lng: Double
)

@Composable
fun ShipMarkerPopUp(
    isVisible: Boolean,
    ship: ShipData?,
    onCallClick: () -> Unit,
    onConversationClick: () -> Unit,
    onClose: () -> Unit
) {
    if (isVisible) {
        ShipDialog(
            onDismissRequest = onClose
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Header(onClose = onClose)
                Image(
                    painter = painterResource(id = R.drawable.ic_ship),
                    contentDescription = null
                )
                Dimensions.space10.HeightSpacer()
                if (ship != null) {
                    Description(shipData = ship)
                }
                Dimensions.space10.HeightSpacer()
                ShipButton(
                    text = stringResource(id = R.string.s10),
                    onClick = onCallClick
                )
                Dimensions.space14.HeightSpacer()
                ShipButton(
                    text = stringResource(id = R.string.s11),
                    type = ShipButtonType.Secondary,
                    onClick = onConversationClick
                )
            }
        }
    }
}

@Composable
private fun Header(
    onClose: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            modifier = Modifier.align(Alignment.CenterEnd).clickable { onClose() },
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null
        )
    }
}

@Composable
private fun Description(
    shipData: ShipData
) {
    val text = buildAnnotatedString {
        pushStyle(Typography.bodyLarge.toSpanStyle())
        append(stringResource(id = R.string.s5).withSuffix(": "))
        pushStyle(Typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle())
        append(shipData.shipName)
        append("\n")

        pop()
        append(stringResource(id = R.string.s6))
        append("\n")

        pushStyle(Typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle())
        append(stringResource(id = R.string.s7).withSuffix(": "))
        append(shipData.location.lat.toString().withSuffix(" "))

        append(stringResource(id = R.string.s8).withSuffix(": "))
        append(shipData.location.lng.toString())
        append("\n")

        pop()
        append(stringResource(id = R.string.s9).withSuffix(": "))
        append(shipData.description)
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}