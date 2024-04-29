package edu.pwr.navcomsys.ships.screens.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.WidthSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun MessageScreen(
    navigation: MessageNavigation
) {
    val viewModel: MessageViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    MessageScreenContent(
        uiState = uiState,
        navigation = navigation
    )
}

@Composable
private fun MessageScreenContent(
    uiState: MessageUiState,
    navigation: MessageNavigation
) {
    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    Column(modifier = Modifier.padding(horizontal = Dimensions.space20)) {
        Dimensions.space40.HeightSpacer()
        Text(
            text = "Ostatnie wiadomości",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Dimensions.space20.HeightSpacer()
        ShipButton(
            text = "+ Nowa wiadomość",
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            type = ShipButtonType.Secondary
        ) {
            navigation.onNewMessageClick()
        }
        LazyColumn {
            item {
                Dimensions.space20.HeightSpacer()
            }

            items(uiState.messages) {
                Message(
                    data = it,
                    onMessageClick = navigation::onConversationClick
                )
            }

            item {
                Dimensions.space40.HeightSpacer()
            }
        }
    }
}

@Composable
private fun Message(
    data: MessageData,
    onMessageClick: (String) -> Unit
) {
    val shipText = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle()) {
            append("Statek: ")
        }
        withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(data.name)
        }
    }
    Column(
        modifier = Modifier.clickable { onMessageClick(data.conversationId) }
    ) {
        Dimensions.space20.HeightSpacer()
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.ic_ship),
                contentDescription = null
            )
            Dimensions.space10.WidthSpacer()
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
                    text = "${data.lastMessage.take(16)}       ${data.time}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        Dimensions.space20.HeightSpacer()
        Divider()
    }
}