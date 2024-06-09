package edu.pwr.navcomsys.ships.screens.phone

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.WidthSpacer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun PhoneScreen(
    navigation: PhoneNavigation
) {
    val viewModel: PhoneViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = PhoneUiInteraction.default(viewModel)

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.navigationEvent.collectLatest { ip ->
                ip?.let {
                    navigation.openCall(ip+"|O")
                }
            }
        }
    }

    PhoneScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction
    )
}

@Composable
private fun PhoneScreenContent(
    uiState: PhoneUiState,
    uiInteraction: PhoneUiInteraction
) {
    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    Column(modifier = Modifier.padding(horizontal = Dimensions.space20)) {
        Dimensions.space40.HeightSpacer()
        Text(
            text = "Ostatnie połącznia",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Dimensions.space20.HeightSpacer()
        TwoTabs(
            uiState = uiState,
            uiInteraction = uiInteraction
        )
        LazyColumn {
            item {
                Dimensions.space20.HeightSpacer()
            }

            items(uiState.calls) {
                Call(
                    data = it,
                    onCallClick = uiInteraction::onCallClick
                )
            }

            item {
                Dimensions.space40.HeightSpacer()
            }
        }
    }
}

@Composable
private fun Call(
    data: CallData,
    onCallClick: (String) -> Unit
) {
    val shipText = buildAnnotatedString {
        withStyle(MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold).toSpanStyle()) {
            append("Statek: ")
        }
        withStyle(MaterialTheme.typography.bodyLarge.toSpanStyle()) {
            append(data.name)
        }
    }
    Column {
        Dimensions.space20.HeightSpacer()
        Row(
            verticalAlignment = Alignment.CenterVertically
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
                    text = "${data.date} ${data.time}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            ShipButton(
                modifier = Modifier.weight(1f),
                text = "Zadzwoń",
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                width = 50.dp
            ) {
                onCallClick(data.ip)
            }
        }
        Dimensions.space20.HeightSpacer()
        Divider()
    }
}

@Composable
private fun TwoTabs(
    uiState: PhoneUiState,
    uiInteraction: PhoneUiInteraction
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        Tab(
            modifier = Modifier.weight(1f),
            title = "Wychodzące",
            isSelected = uiState.selectedTab == PhoneTab.Outgoing,
            onClick = { uiInteraction.onSelectTab(PhoneTab.Outgoing) }
        )
        Dimensions.space10.WidthSpacer()
        Tab(
            modifier = Modifier.weight(1f),
            title = "Przychodzące",
            isSelected = uiState.selectedTab == PhoneTab.Incoming,
            onClick = { uiInteraction.onSelectTab(PhoneTab.Incoming) }
        )
    }
}

@Composable
private fun Tab(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val textStyle = if (isSelected)
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
    else
        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium)

    Box(modifier = modifier.then(Modifier.clickable { onClick() })) {
        Column(modifier = Modifier.align(Alignment.Center)) {
            Text(
                text = title,
                style = textStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
            Dimensions.space4.HeightSpacer()
        }
        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary)
                    .align(Alignment.BottomCenter)
            )
        }
    }
}