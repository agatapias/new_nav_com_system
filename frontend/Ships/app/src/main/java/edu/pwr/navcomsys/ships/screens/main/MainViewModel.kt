package edu.pwr.navcomsys.ships.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.mapbox.mapboxsdk.annotations.Marker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onMapLoaded() {
        Log.d("Map", "onMapLoaded called")
        _uiState.update { it.copy(isLoading = false) }
    }

    fun onShipClick(marker: Marker) {
        _uiState.update {
            it.copy(
                isPopUpVisible = true,
                marker = marker
            )
        }
    }

    fun onClosePopUp() {
        _uiState.update { it.copy(isPopUpVisible = false) }
    }
}