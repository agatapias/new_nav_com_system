package edu.pwr.navcomsys.ships.screens.main

import edu.pwr.navcomsys.ships.data.dto.LocationDto

interface MainUiInteraction {
    fun onMapLoaded()
    fun onShipClick(ship: LocationDto)
    fun onClosePopUp()

    companion object {
        fun default(viewModel: MainViewModel? = null) = object : MainUiInteraction {
            override fun onMapLoaded() {
                viewModel?.onMapLoaded()
            }

            override fun onShipClick(ship: LocationDto) {
                viewModel?.onShipClick(ship)
            }

            override fun onClosePopUp() {
                viewModel?.onClosePopUp()
            }

        }
    }
}