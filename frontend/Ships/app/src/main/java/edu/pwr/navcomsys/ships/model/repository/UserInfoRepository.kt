package edu.pwr.navcomsys.ships.model.repository

import edu.pwr.navcomsys.ships.data.dto.UserInfoDto
import edu.pwr.navcomsys.ships.data.enums.ResponseCode
import edu.pwr.navcomsys.ships.model.datasource.remote.UserInfoRemoteDataSource

class UserInfoRepository(
    private val userInfoRemoteDataSource: UserInfoRemoteDataSource
) {
    suspend fun updateUserInfo(userInfoDto: UserInfoDto) : ResponseCode {
        val result = userInfoRemoteDataSource.updateUserInfo(userInfoDto)

        return when(result.code()) {
            200 -> ResponseCode.SUCCESS
            409 -> ResponseCode.CONFLICT
            else -> ResponseCode.FAILURE
        }
    }
}