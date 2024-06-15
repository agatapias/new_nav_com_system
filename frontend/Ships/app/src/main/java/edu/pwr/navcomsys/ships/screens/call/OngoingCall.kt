package edu.pwr.navcomsys.ships.screens.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.theme.Dark
import edu.pwr.navcomsys.ships.ui.theme.DarkBlue
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.Red
import edu.pwr.navcomsys.ships.ui.theme.ShipsTheme

private val colorStops = arrayOf(
    0.0f to DarkBlue,
    1f to Dark
)

@Composable
fun OngoingCall(
    uiState: CallUiState,
    uiInteraction: CallUiInteraction
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(*colorStops))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    vertical = Dimensions.space40,
                    horizontal = Dimensions.space40
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Dimensions.space40.HeightSpacer()
                Text(
                    text = "Połączenie",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Dimensions.space2.HeightSpacer()
                Text(
                    text = uiState.caller,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
                Dimensions.space22.HeightSpacer()
                Text(
                    text = uiState.callTime,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val text = if (uiState.isRecording) "Stop" else "Nagrywaj"
                ShipButton(
                    modifier = Modifier.fillMaxWidth(),
                    text = text,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    onClick = { uiInteraction.onRecordPress() }
                )
                Dimensions.space22.HeightSpacer()
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(color = Red)
                        .clickable { uiInteraction.endCall() }
                ) {
                    Image(
                        modifier = Modifier
                            .size(45.dp)
                            .align(Alignment.Center),
                        painter = painterResource(id = R.drawable.ic_phone_down),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun OngoingCallPreview() {
    ShipsTheme() {
        OngoingCall(uiState = CallUiState(), uiInteraction = CallUiInteraction.empty())
    }
}