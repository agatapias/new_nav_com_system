package edu.pwr.navcomsys.ships.screens.main

import com.mapbox.mapboxsdk.annotations.Marker

interface MainUiInteraction {
    fun onMapLoaded()
    fun onShipClick(marker: Marker)
    fun onClosePopUp()

    companion object {
        fun default(viewModel: MainViewModel? = null) = object : MainUiInteraction {
            override fun onMapLoaded() {
                viewModel?.onMapLoaded()
            }

            override fun onShipClick(marker: Marker) {
                viewModel?.onShipClick(marker)
            }

            override fun onClosePopUp() {
                viewModel?.onClosePopUp()
            }

        }
    }
}