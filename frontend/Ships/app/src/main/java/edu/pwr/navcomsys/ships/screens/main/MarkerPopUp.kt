package edu.pwr.navcomsys.ships.screens.main

import android.preference.PreferenceActivity
import android.util.EventLogTags
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.Popup
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.extenstion.withSuffix
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.component.ShipDialog
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.Typography

data class ShipData(
    val name: String,
    val location: ShipLocation,
    val description: String
) {
    companion object {
        fun mock() = ShipData(
            name = "Rosa",
            location = ShipLocation(lat = 12.44f, lng = 10.11f),
            description = "Best ship on earth"
        )
    }
}

data class ShipLocation(
    val lat: Float,
    val lng: Float
)

@Composable
fun MarkerPopUp(
    isVisible: Boolean,
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
                Description(shipData = ShipData.mock())
                Dimensions.space10.HeightSpacer()
                ShipButton(
                    text = stringResource(id = R.string.s10),
                    onClick = {}
                )
                Dimensions.space14.HeightSpacer()
                ShipButton(
                    text = stringResource(id = R.string.s11),
                    type = ShipButtonType.Secondary,
                    onClick = {}
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
        append(shipData.name)
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