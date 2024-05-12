package edu.pwr.navcomsys.ships.model.datasource.remote.impl

import edu.pwr.navcomsys.ships.model.datasource.remote.SignalRemoteDataSource
import retrofit2.Retrofit

class SignalRemoteDataSourceImpl(retrofit: Retrofit) :
    SignalRemoteDataSource by retrofit.create(SignalRemoteDataSource::class.java)