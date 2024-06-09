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
            username = "Miś",
            shipName = "Miś's ship",
            description = "Best ship",
            ipAddress = "103.42.56.9",
            xCoordinate = 14.987,
            yCoordinate = 13.232
        )
    }
}
