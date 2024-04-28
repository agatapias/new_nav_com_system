package edu.pwr.navcomsys.ships.screens.phone

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PhoneViewModel() : ViewModel() {
    private val _uiState = MutableStateFlow(PhoneUiState())
    val uiState = _uiState.asStateFlow()

    fun onCallClick(phone: String) {

    }

    fun onSelectTab(tab: PhoneTab) {
        _uiState.update {
            it.copy(selectedTab = tab)
        }
    }
}