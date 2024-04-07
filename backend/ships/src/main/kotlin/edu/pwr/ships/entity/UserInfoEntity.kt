package edu.pwr.ships.entity

import edu.pwr.ships.dto.UserInfoDto
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.GeoIndexed
import java.io.Serializable


@RedisHash("UserInfo")
class UserInfoEntity : Serializable {
    var id: String? = null
    var username: String = ""

    @GeoIndexed
    var location: Point? = null

    fun toDto() : UserInfoDto {
        return UserInfoDto(username, location?.x!!, location?.y!!)
    }
}

