package edu.pwr.navcomsys.ships.screens.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.component.ShipButton
import edu.pwr.navcomsys.ships.ui.component.ShipButtonType
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import edu.pwr.navcomsys.ships.ui.theme.HeightSpacer
import org.koin.androidx.compose.koinViewModel

@Composable
fun AccountScreen() {
    val viewModel: AccountViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = AccountUiInteraction.default(viewModel)

    AccountScreenContent(uiState, uiInteraction)
}

@Composable
private fun AccountScreenContent(
    uiState: AccountUiState,
    uiInteraction: AccountUiInteraction
) {
    val username = remember { mutableStateOf(uiState.user?.username) }
    val shipName = remember { mutableStateOf(uiState.user?.shipName) }
    val description = remember { mutableStateOf(uiState.user?.description) }

    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    Column(modifier = Modifier.padding(horizontal = Dimensions.space20)) {
        Dimensions.space40.HeightSpacer()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = "Twoje dane",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.ic_ship),
                contentDescription = null
            )
        }
        Dimensions.space20.HeightSpacer()
        AccountItem(
            title = "Nazwa użytkownika",
            value = uiState.user?.username,
            textFieldValue = username,
            isBold = uiState.user?.username != null,
            isEditingMode = uiState.isEditingMode
        )
        Dimensions.space20.HeightSpacer()
        AccountItem(
            title = "Nazwa statku",
            value = uiState.user?.shipName,
            textFieldValue = shipName,
            isBold = uiState.user?.shipName != null,
            isEditingMode = uiState.isEditingMode
        )
        Dimensions.space20.HeightSpacer()
        AccountItem(
            title = "Opis",
            value = uiState.user?.description,
            textFieldValue = description,
            isBold = false,
            isEditingMode = uiState.isEditingMode
        )
        Box(modifier = Modifier.weight(1f))
        if (uiState.isEditingMode) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimensions.space20)
            ) {
                ShipButton(
                    modifier = Modifier.weight(1f),
                    text = "Anuluj",
                    type = ShipButtonType.Alternative
                ) {
                    uiInteraction.onCancelEditClick()
                }
                ShipButton(
                    modifier = Modifier.weight(1f),
                    text = "Zapisz",
                    type = ShipButtonType.Primary
                ) {
                    uiInteraction.onSaveClick(username.value ?: "", shipName.value ?: "", description.value ?: "")
                }
            }
        } else {
            ShipButton(
                modifier = Modifier.fillMaxWidth(),
                text = "Edytuj dane",
                type = ShipButtonType.Alternative
            ) {
                uiInteraction.onEditDataClick()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ColumnScope.AccountItem(
    title: String,
    value: String?,
    textFieldValue: MutableState<String?>,
    isBold: Boolean,
    isEditingMode: Boolean
) {
    val fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
    Text(
        text = title,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface
    )
    Dimensions.space8.HeightSpacer()
    if (isEditingMode) {
        OutlinedTextField(
            value = textFieldValue.value ?: "",
            onValueChange = { textFieldValue.value = it }
        )
    } else {
        Text(
            text = value ?: "Brak informacji. Uzupełnij dane.",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = fontWeight),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}