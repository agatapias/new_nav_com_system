package edu.pwr.navcomsys.ships.model.datasource.remote

import edu.pwr.navcomsys.ships.data.dto.SignalDto
import retrofit2.Response
import retrofit2.http.Body

interface SignalRemoteDataSource {

    suspend fun sendSignal(
        @Body signalDto: SignalDto
    ): Response<Unit>
    
}
