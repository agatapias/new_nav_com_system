package edu.pwr.navcomsys.ships.screens.shiplist

import edu.pwr.navcomsys.ships.data.dto.LocationDto

interface ShipListUiInteraction {
    fun onDetailsClick(ship: LocationDto)
    fun onCloseDialog()
    fun onCall(ip: String)

    companion object {
        fun default(
            viewModel: ShipListViewModel
        ) = object : ShipListUiInteraction {

            override fun onDetailsClick(ship: LocationDto) {
                viewModel.onDetailsClick(ship)
            }

            override fun onCloseDialog() {
                viewModel.onCloseDialog()
            }

            override fun onCall(ip: String) {
                viewModel.onCallClick(ip)
            }
        }
    }
}