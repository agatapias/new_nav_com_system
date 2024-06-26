package edu.pwr.navcomsys.ships.model.backgroundservice

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.Timer
import kotlin.concurrent.timerTask

class UserInfoService : Service() {
    private val locationManager: FusedLocationProviderClient by inject()
    private val userInfoRepository: UserInfoRepository by inject()
    private val peerRepository: PeerRepository by inject()

    private var lastLocation: Location? = null

    override fun onCreate() {
        Log.d("UserInfoService", "onCreate called")
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // whatever
        } else {
            locationManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Handle the location object
                        lastLocation = it
                        val latitude = it.latitude
                        val longitude = it.longitude
                        Log.d("UserInfoService","Latitude: $latitude, Longitude: $longitude")
                    }
                }
                .addOnFailureListener {
                    // Handle the failure
                    Log.e("UserInfoService","getting last location failed", it)
                }
        }
        startService()
        super.onCreate()
    }

    override fun onBind(p0: Intent?): IBinder? {
        Log.d("UserInfoService", "onBind called")
        return null
    }

    private fun startService() {
        Log.d("UserInfoService", "startService called")
        CoroutineScope(Dispatchers.Default).launch {
            val timer = Timer()

            // Define the task to be repeated
            val task = timerTask {
                CoroutineScope(Dispatchers.Default).launch {
                    val user = userInfoRepository.getUser()
                    Log.d("UserInfoService", "user: ${user?.username}")
                    val location = lastLocation
                    Log.d(
                        "UserInfoService",
                        "last location: ${lastLocation?.latitude}, ${lastLocation?.longitude}"
                    )

                    for (device in peerRepository.connectedDevices) {
                        if (user != null && location != null && device.deviceName != peerRepository.deviceName) {
                            val locationDto = LocationDto(
                                username = user.username,
                                shipName = user.shipName,
                                description = user.description,
                                ipAddress = peerRepository.getOwnIpInfo().ipAddress,
                                xCoordinate = location.latitude,
                                yCoordinate = location.longitude
                            )
                            val json =
                                peerRepository.convertToJson(locationDto, MessageType.DEVICE_INFO)
                            peerRepository.sendMessage(device.ipAddress, json)
                        }
                    }
                }
            }

            timer.schedule(task, 0L, 5000)
        }
    }
}