package edu.pwr.navcomsys.ships.screens.conversation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConversationViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(ConversationUiState())
    val uiState = _uiState.asStateFlow()

    fun onInputChange(value: String) {
        _uiState.update {
            it.copy(input = value)
        }
    }

    fun onSendMessage(value: String) {
        // TODO
    }
}