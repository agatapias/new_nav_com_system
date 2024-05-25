package edu.pwr.navcomsys.ships.screens.account

import edu.pwr.navcomsys.ships.data.database.User

data class AccountUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isEditingMode: Boolean = false
)
