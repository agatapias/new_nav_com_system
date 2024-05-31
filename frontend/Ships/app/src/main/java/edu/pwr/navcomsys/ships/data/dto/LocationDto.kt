package edu.pwr.navcomsys.ships.data.dto

data class LocationDto(
    val username: String,
    val shipName: String,
    val description: String,
    val ipAddress: String,
    val xCoordinate: Double,
    val yCoordinate: Double
)
