package edu.pwr.navcomsys.ships.screens.account

interface AccountUiInteraction {
    fun onEditDataClick()
    fun onCancelEditClick()
    fun onSaveClick(
        username: String,
        shipName: String,
        description: String
    )

    companion object {
        fun default(
            viewModel: AccountViewModel
        ) = object : AccountUiInteraction {
            override fun onEditDataClick() {
                viewModel.onEditDataClick()
            }

            override fun onCancelEditClick() {
                viewModel.onCancelEditClick()
            }

            override fun onSaveClick(
                username: String,
                shipName: String,
                description: String
            ) {
                viewModel.onSaveClick(username, shipName, description)
            }

        }
    }
}