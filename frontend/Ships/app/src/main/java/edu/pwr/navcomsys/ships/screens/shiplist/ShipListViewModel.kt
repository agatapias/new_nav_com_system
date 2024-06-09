package edu.pwr.navcomsys.ships.screens.shiplist

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

class ShipListViewModel(
    private val peerRepository: PeerRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ShipListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.Default) {
            peerRepository.getLocations().collectLatest { locations ->
                Log.d("Map", "new ship locations in vm: $locations")
                _uiState.update { it.copy(
                    ships = locations
                ) }
            }
        }
    }

    fun onDetailsClick(ship: LocationDto) {
        _uiState.update { it.copy(
            shipDialogData = ship
        ) }
    }

    fun onCloseDialog() {
        _uiState.update { it.copy(
            shipDialogData = null
        ) }
    }
}