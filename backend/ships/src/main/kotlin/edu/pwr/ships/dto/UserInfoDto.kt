package edu.pwr.ships.dto

import edu.pwr.ships.entity.UserInfoEntity
import org.springframework.data.geo.Point

data class UserInfoDto(
    val username: String,
    val longitude: Double,
    val latitude: Double
) {
    fun toEntity() : UserInfoEntity {
        val entity = UserInfoEntity()
        entity.username = username
        entity.location = Point(longitude, latitude)
        return entity
    }
}
