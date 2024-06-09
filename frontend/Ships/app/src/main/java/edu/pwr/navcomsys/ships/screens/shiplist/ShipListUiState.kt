package edu.pwr.navcomsys.ships.screens.shiplist

import edu.pwr.navcomsys.ships.data.dto.LocationDto

data class ShipListUiState(
    val ships: List<LocationDto> = emptyList(),
    val shipDialogData: LocationDto? = null,
    val isLoading: Boolean = false,
)
