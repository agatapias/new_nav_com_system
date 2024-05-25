package edu.pwr.navcomsys.ships.model.datasource.remote.impl

import edu.pwr.navcomsys.ships.model.datasource.remote.UserInfoRemoteDataSource
import retrofit2.Retrofit

class UserInfoRemoteDataSourceImpl(retrofit: Retrofit) :
    UserInfoRemoteDataSource by retrofit.create(UserInfoRemoteDataSource::class.java)