package edu.pwr.ships.repository

import edu.pwr.ships.entity.UserInfoEntity
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface UserInfoRepository : CrudRepository<UserInfoEntity, String> {
    fun findByUsername(username: String) : UserInfoEntity?
    fun findByLocationNear(location: Point, distance: Distance): List<UserInfoEntity?>
}