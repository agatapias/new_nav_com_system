package edu.pwr.navcomsys.ships.screens.main

import edu.pwr.navcomsys.ships.data.dto.LocationDto

data class MainUiState(
    val shipLocations: List<LocationDto> = emptyList(),
    val isLoading: Boolean = true,
    val isPopUpVisible: Boolean = false,
    val ship: LocationDto? = null
)