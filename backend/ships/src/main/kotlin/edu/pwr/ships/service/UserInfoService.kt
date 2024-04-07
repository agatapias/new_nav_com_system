package edu.pwr.ships.service

import edu.pwr.ships.dto.UserInfoDto
import edu.pwr.ships.entity.UserInfoEntity
import edu.pwr.ships.repository.UserInfoRepository
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.GeoResult
import org.springframework.data.geo.GeoResults
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.domain.geo.Metrics
import org.springframework.stereotype.Service
import java.util.function.Function
import java.util.stream.Collectors


const val radius = 1

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoRepository,
) {

    fun updateInformation(dto: UserInfoDto) : UserInfoDto {
        val existing = userInfoRepository.findByUsername(dto.username)
        if (existing != null) {
            userInfoRepository.delete(existing)
        }

        val toSave = dto.toEntity()
        return userInfoRepository.save(toSave).toDto()
    }


    fun findUsersWithinRadius(username: String, radius: Double): List<UserInfoDto> {
        val user = userInfoRepository.findByUsername(username)
        val point = Point(user?.location?.x!!, user?.location?.y!!)
        val distance = Distance(radius/50, Metrics.MILES)
        return userInfoRepository.findByLocationNear(point, distance)
            .mapNotNull { it?.toDto() }
    }
}