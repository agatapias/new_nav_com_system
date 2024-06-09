package edu.pwr.navcomsys.ships.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import edu.pwr.navcomsys.ships.R

sealed class Screen(
    val path: String,
    @DrawableRes val icon: Int? = null,
    @StringRes val description: Int? = null,
    @StringRes val label: Int? = null,
    val tag: String = ""
) {
    object Dashboard : Screen(
        path = "dashboard",
        icon = R.drawable.ic_world,
        description = R.string.s3,
        tag = "dashboard"
    )

    object Calls : Screen(
        path = "calls",
        icon = R.drawable.ic_phone,
        description = R.string.s1,
        tag = "calls"
    )

    object Messages : Screen(
        path = "messages",
        icon = R.drawable.ic_message,
        description = R.string.s2,
        tag = "messages"
    )

    object Account : Screen(
        path = "account",
        icon = R.drawable.ic_boat,
        description = R.string.s4,
        tag = "account"
    )

    object Main : Screen(
        path = "main",
    )

    object Conversation : Screen(
        path = "conversation",  // TODO: /{id}
    )

    object ShipList : Screen(
        path = "shipList",
    )
}