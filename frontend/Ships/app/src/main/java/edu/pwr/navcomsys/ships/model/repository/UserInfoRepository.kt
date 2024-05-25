package edu.pwr.navcomsys.ships.model.repository

import android.util.Log
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

    suspend fun getUser(): User? {
        return userLocalDataSource.getUser()
    }

    suspend fun updateUserInfo(userInfoDto: UserInfoDto) : ResponseCode {
        Log.d("UserInfoRepo", "updateUserInfo called")
        val result = userInfoRemoteDataSource.updateUserInfo(userInfoDto)
        val code = result.code()
        Log.d("UserInfoRepo", "result: $code")

        return when(code) {
            200 -> ResponseCode.SUCCESS
            409 -> ResponseCode.CONFLICT
            else -> ResponseCode.FAILURE
        }
    }
}