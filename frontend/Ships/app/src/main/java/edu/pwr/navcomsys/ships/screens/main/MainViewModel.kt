package edu.pwr.navcomsys.ships.screens.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val peerRepository: PeerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    fun onMapLoaded() {
        Log.d("Map", "onMapLoaded called")
        _uiState.update { it.copy(isLoading = false) }
        viewModelScope.launch(Dispatchers.Default) {
            peerRepository.getLocations().collectLatest { locations ->
                _uiState.update {
                    it.copy(
                        shipLocations = locations
                    )
                }
            }
        }

        viewModelScope.launch(Dispatchers.Default) {
            peerRepository.getOwnLocation().collectLatest { loc ->
                _uiState.update { it.copy(
                    yourLocation = loc
                ) }
            }
        }
    }

    fun onShipClick(ship: LocationDto) {
        _uiState.update {
            it.copy(
                isPopUpVisible = true,
                ship = ship
            )
        }
    }

    fun onClosePopUp() {
        _uiState.update { it.copy(isPopUpVisible = false) }
    }
}