package edu.pwr.navcomsys.ships.screens.conversation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import edu.pwr.navcomsys.ships.ui.theme.WidthSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun ConversationScreen(
    navigation: ConversationNavigation
) {
    val viewModel: ConversationViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = ConversationUiInteraction.default(viewModel)

    LaunchedEffect(Unit) {
        viewModel.setUsername(navigation.name)
        Log.d("Conv", "name: ${navigation.name}")
    }

    ConversationScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConversationScreenContent(
    uiState: ConversationUiState,
    uiInteraction: ConversationUiInteraction
) {
    Column {
        TopBar(
            data = uiState.accountData
        )
        Messages(
            modifier = Modifier.weight(1f),
            uiState = uiState
        )
        if (uiState.isConnected) {
            BottomBar(
                input = uiState.input,
                onValueChange = uiInteraction::onValueChange,
                onSendMessage = uiInteraction::onSendMessage,
            )
        }
    }
}

@Composable
private fun Messages(
    modifier: Modifier = Modifier,
    uiState: ConversationUiState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = Dimensions.space20),
        reverseLayout = true
    ) {
        itemsIndexed(uiState.messages) { index, it ->
            Message(
                data = it,
                isFirst = index == 0
            )
            Dimensions.space22.HeightSpacer()
        }
    }
}

@Composable
private fun Message(
    data: ConversationMessageData,
    isFirst: Boolean
) {
    if (data.isYourMessage) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            YourMessage(
                data = data,
                isFirst = isFirst
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            OtherMessage(data = data)
        }
    }

}

@Composable
private fun OtherMessage(
    data: ConversationMessageData
) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = R.drawable.ic_ship),
            contentDescription = null
        )
        Dimensions.space12.WidthSpacer()
        Card(
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                modifier = Modifier.padding(Dimensions.space10),
                text = data.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun YourMessage(
    data: ConversationMessageData,
    isFirst: Boolean
) {
    Row(
        verticalAlignment = Alignment.Bottom
    ) {
        Column {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.widthIn(max = 300.dp),
            ) {
                Text(
                    modifier = Modifier.padding(Dimensions.space10),
                    text = data.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.surface
                )
            }
            if (isFirst) {
                Dimensions.space2.HeightSpacer()
                Text(
                    modifier = Modifier.padding(Dimensions.space10),
                    text = "Wysłano: ${data.read}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun TopBar(
    modifier: Modifier = Modifier,
    data: AccountData
) {
    Row(
        modifier = modifier.padding(
            horizontal = Dimensions.space18,
            vertical = Dimensions.space10
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(imageVector = Icons.Default.ArrowBack, contentDescription = "Return")
        Dimensions.space12.WidthSpacer()
        Image(
            modifier = Modifier.size(30.dp),
            painter = painterResource(id = R.drawable.ic_ship),
            contentDescription = null
        )
        Dimensions.space8.WidthSpacer()
        Text(
            text = "${data.shipName} | ${data.personName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Box(modifier = Modifier.weight(1f))
        Image(
            modifier = Modifier.size(25.dp),
            painter = painterResource(id = R.drawable.ic_phone),
            contentDescription = null
        )
    }
}

@Composable
private fun BottomBar(
    modifier: Modifier = Modifier,
    input: String,
    onValueChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
) {
    Column {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    horizontal = Dimensions.space20,
                    vertical = Dimensions.space20
                )
        ) {
            Card(
                shape = CircleShape,
                border = BorderStroke(width = 1.5.dp, color = MaterialTheme.colorScheme.primary),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp)
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    BasicTextField(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        value = input,
                        textStyle = MaterialTheme.typography.bodyLarge,
                        onValueChange = {
                            onValueChange(it)
                        },
                    )
                }
            }
        }
        Button(onClick = { onSendMessage(input) }, enabled = true) {
            Text(text = "Send")
        }
    }

}