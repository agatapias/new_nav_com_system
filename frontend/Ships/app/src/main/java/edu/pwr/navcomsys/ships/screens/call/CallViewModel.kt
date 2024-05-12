package edu.pwr.navcomsys.ships.screens.call

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CallViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CallUiState())
    val uiState = _uiState.asStateFlow()

    fun onDeclineCall() {

    }
}