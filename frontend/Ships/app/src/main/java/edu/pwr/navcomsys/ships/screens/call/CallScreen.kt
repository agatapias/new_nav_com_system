package edu.pwr.navcomsys.ships.screens.call

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CallScreen(
    navigation: CallNavigation
) {
    val viewModel: CallViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = CallUiInteraction.default(viewModel)

    LaunchedEffect(Unit) {
        viewModel.init(navigation.ip, navigation.type, navigation.toUsername ?: "")
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.navigationEvent.collect {
                Log.d("Call", "navigating back, value: $it")
                if (it == true) {
                    navigation.goBack()
                }
            }
        }
    }

    CallScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction
    )
}

@Composable
private fun CallScreenContent(
    uiState: CallUiState,
    uiInteraction: CallUiInteraction
) {
    when (uiState.callStatus) {
        CallStatus.Incoming -> IncomingCall(
            uiState = uiState,
            uiInteraction = uiInteraction
        )
        
        CallStatus.Ongoing -> OngoingCall(
            uiState = uiState,
            uiInteraction = uiInteraction
        )
        
        CallStatus.Outgoing -> OutgoingCall(
            uiState = uiState,
            uiInteraction = uiInteraction
        )
    }
    
}