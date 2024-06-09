package edu.pwr.navcomsys.ships.screens.shiplist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.component.ShipData.Companion.toShipData
import edu.pwr.navcomsys.ships.ui.component.ShipMarkerPopUp
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.WidthSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun ShipListScreen(
    navigation: ShipListNavigation
) {
    val viewModel: ShipListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = ShipListUiInteraction.default(viewModel)
    ShipListScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction,
        navigation = navigation
    )
}

@Composable
private fun ShipListScreenContent(
    uiState: ShipListUiState,
    uiInteraction: ShipListUiInteraction,
    navigation: ShipListNavigation
) {
    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    Column(modifier = Modifier.padding(horizontal = Dimensions.space20)) {
        Dimensions.space40.HeightSpacer()
        Text(
            text = "Statki w pobliżu",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Dimensions.space20.HeightSpacer()
        LazyColumn {
            item {
                Dimensions.space20.HeightSpacer()
            }

            items(uiState.ships) {
                ShipItem(
                    data = it,
                    uiInteraction = uiInteraction,
                    navigation = navigation
                )
            }

            item {
                Dimensions.space40.HeightSpacer()
            }
        }
    }

    ShipMarkerPopUp(
        isVisible = uiState.shipDialogData != null,
        ship = uiState.shipDialogData?.toShipData(),
        onCallClick = {/*TODO*/ },
        onConversationClick = {
            uiState.shipDialogData?.shipName?.let {
                navigation.openConversation(it)
            }
        },
        onClose = uiInteraction::onCloseDialog
    )
}

@Composable
private fun ShipItem(
    data: LocationDto,
    uiInteraction: ShipListUiInteraction,
    navigation: ShipListNavigation
) {
    val shipText = buildAnnotatedString {
        withStyle(
            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle()
        ) {
            append("Statek: ")
        }
        withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(data.shipName)
        }
    }
    Column {
        Dimensions.space20.HeightSpacer()
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = shipText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Dimensions.space4.HeightSpacer()
                Text(
                    text = "Lat: ${data.xCoordinate}, Lon: ${data.yCoordinate}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                modifier = Modifier.clickable {
                    uiInteraction.onDetailsClick(data)
                },
                text = "szczegóły",
                style = MaterialTheme.typography.bodyLarge.copy(
                    textDecoration = TextDecoration.Underline,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Dimensions.space20.HeightSpacer()
        Row {
            ShipButton(
                modifier = Modifier.weight(1f),
                text = "Zadzwoń",
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            ) {
                // TODO
            }
            Dimensions.space14.WidthSpacer()
            ShipButton(
                modifier = Modifier.weight(1f),
                text = "Napisz",
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                type = ShipButtonType.Secondary
            ) {
                navigation.openConversation(data.username)
            }
        }
        Dimensions.space20.HeightSpacer()
        Divider()
    }
}