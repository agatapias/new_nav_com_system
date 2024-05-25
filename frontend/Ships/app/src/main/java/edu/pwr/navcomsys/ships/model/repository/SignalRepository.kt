package edu.pwr.navcomsys.ships.model.repository

import edu.pwr.navcomsys.ships.data.dto.SignalDto
import edu.pwr.navcomsys.ships.data.enums.ResponseCode
import edu.pwr.navcomsys.ships.model.datasource.remote.SignalRemoteDataSource

class SignalRepository(
    private val signalRemoteDataSource: SignalRemoteDataSource
) {

    suspend fun sendSignal(signalDto: SignalDto) : ResponseCode {
        val result = signalRemoteDataSource.sendSignal(signalDto)

        return when(result.code()) {
            200 -> ResponseCode.SUCCESS
            409 -> ResponseCode.CONFLICT
            else -> ResponseCode.FAILURE
        }
    }
}