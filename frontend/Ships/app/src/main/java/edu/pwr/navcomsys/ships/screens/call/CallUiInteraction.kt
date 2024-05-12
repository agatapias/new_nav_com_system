package edu.pwr.navcomsys.ships.screens.call

interface CallUiInteraction {
    companion object {
        fun default(
            viewModel: CallViewModel
        ) = object : CallUiInteraction {

        }
    }
}