package edu.pwr.navcomsys.ships.model.datasource.remote

import edu.pwr.navcomsys.ships.data.dto.UserInfoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.PUT

interface UserInfoRemoteDataSource {

    @PUT("/user")
    suspend fun updateUserInfo(
        @Body userInfoDto: UserInfoDto
    ): Response<UserInfoDto>
}