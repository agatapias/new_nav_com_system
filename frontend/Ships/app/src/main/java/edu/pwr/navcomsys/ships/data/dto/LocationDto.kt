package edu.pwr.navcomsys.ships.data.dto

data class LocationDto(
    val username: String,
    val shipName: String,
    val description: String,
    val ipAddress: String,
    val xCoordinate: Double,
    val yCoordinate: Double
) {
    companion object {
        fun mock() = LocationDto(
            username = "andrew",
            shipName = "Statek Rosa",
            description = "Duży statek towarowy",
            ipAddress = "103.42.56.9",
            xCoordinate = 51.123733,
            yCoordinate = 17.085849
        )
    }
}
