package edu.pwr.navcomsys.ships.screens.main

import com.mapbox.mapboxsdk.annotations.Marker

data class MainUiState(
    val isLoading: Boolean = true,
    val isPopUpVisible: Boolean = false,
    val marker: Marker? = null
)