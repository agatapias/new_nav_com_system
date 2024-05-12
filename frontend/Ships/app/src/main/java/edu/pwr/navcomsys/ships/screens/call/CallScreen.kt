package edu.pwr.navcomsys.ships.screens.call

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun CallScreen() {
    val viewModel: CallViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = CallUiInteraction.default(viewModel)
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
        CallStatus.Incoming -> IncomingCall(uiState = uiState)
        
        CallStatus.Ongoing -> OngoingCall(uiState = uiState)
        
        CallStatus.Outgoing -> OutgoingCall(uiState = uiState)
    }
    
}