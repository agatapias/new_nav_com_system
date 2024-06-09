package edu.pwr.navcomsys.ships.screens.main

import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.ui.component.ShipLocation

data class MainUiState(
    val yourLocation: ShipLocation? = null,
    val shipLocations: List<LocationDto> = emptyList(),
    val isLoading: Boolean = true,
    val isPopUpVisible: Boolean = false,
    val ship: LocationDto? = null
)