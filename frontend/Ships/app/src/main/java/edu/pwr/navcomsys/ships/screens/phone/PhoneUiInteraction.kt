package edu.pwr.navcomsys.ships.screens.phone

interface PhoneUiInteraction {
    fun onCallClick(phone: String)
    fun onSelectTab(tab: PhoneTab)

    companion object {
        fun default(
            viewModel: PhoneViewModel
        ) = object : PhoneUiInteraction {
            override fun onCallClick(phone: String) {
                viewModel.onCallClick(phone)
            }

            override fun onSelectTab(tab: PhoneTab) {
                viewModel.onSelectTab(tab)
            }
        }
    }
}