package edu.pwr.navcomsys.ships.model.repository

import edu.pwr.navcomsys.ships.data.database.User
import edu.pwr.navcomsys.ships.data.dto.UserInfoDto
import edu.pwr.navcomsys.ships.data.enums.ResponseCode
import edu.pwr.navcomsys.ships.model.datasource.local.UserLocalDataSource
import edu.pwr.navcomsys.ships.model.datasource.remote.UserInfoRemoteDataSource

class UserInfoRepository(
    private val userLocalDataSource: UserLocalDataSource,
    private val userInfoRemoteDataSource: UserInfoRemoteDataSource
) {
    suspend fun saveUser(user: User) {
        userLocalDataSource.deleteAll()
        userLocalDataSource.insert(user)
    }

    suspend fun getUser(): User {
        return userLocalDataSource.getUser()
    }

    suspend fun updateUserInfo(userInfoDto: UserInfoDto) : ResponseCode {
        val result = userInfoRemoteDataSource.updateUserInfo(userInfoDto)

        return when(result.code()) {
            200 -> ResponseCode.SUCCESS
            409 -> ResponseCode.CONFLICT
            else -> ResponseCode.FAILURE
        }
    }
}