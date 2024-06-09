package edu.pwr.navcomsys.ships.model.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.gson.Gson
import edu.pwr.navcomsys.ships.data.dto.IPBroadcastDto
import edu.pwr.navcomsys.ships.data.dto.IPInfoDto
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.data.dto.MessageDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import edu.pwr.navcomsys.ships.ui.component.ShipLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException
import java.util.Timer
import java.util.TimerTask


private const val TAG = "PeerRepository"

class PeerRepository(
    private val userInfoRepository: UserInfoRepository,
    locationManager: FusedLocationProviderClient,
    context: Context
) {
    var connectedDevices: List<IPInfoDto> = mutableListOf()
    val locationFlow: MutableStateFlow<List<LocationDto>> = MutableStateFlow(emptyList())
    var deviceName: String? = null

    private val ownLocationFlow: MutableStateFlow<ShipLocation?> = MutableStateFlow(null)
    private var lastLocation: Location? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()
    private val hostTimerMap: MutableMap<String, Timer> = mutableMapOf()

    init {
        mockLocations()
        // Listen to location changes
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("PeerRepository","Lacking permissions")
            // whatever
        } else {
            locationManager.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let { loc ->
                        Log.d("PeerRepository","new location: $loc")
                        lastLocation = loc
                        ownLocationFlow.update {
                            ShipLocation(
                                lat = loc.latitude,
                                lng = loc.longitude
                            )
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle the failure
                    Log.e("PeerRepository","getting last location failed", it)
                }
        }
    }

    fun sendMessage(host: String, msg: String) {
        scope.launch {
            withContext(Dispatchers.IO) {
                Log.d(TAG, "sendMessage called")
                val port = 8888
                val socket = Socket()
                val buf = msg.toByteArray()
                try {
                    /**
                     * Create a client socket with the host,
                     * port, and timeout information.
                     */
                    socket.bind(null)
                    socket.connect((InetSocketAddress(host, port)), 10000)

                    val outputStream = socket.getOutputStream()
                    outputStream.write(buf, 0, buf.size)
                    Log.d(TAG, "broadcast receiver")
                    Log.d(TAG, buf.decodeToString())
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(TAG, "sendMessage error", e)
                    Log.d(TAG, "${e.message} this is error")
                    socket.takeIf { it.isConnected }?.apply {
                        close()
                    }
                }
            }
        }
    }

    fun sendIPInfo(ownerHost: String) {
        val deviceName = this.deviceName
        val ip = getIpAddress()

        Log.d(TAG, "deviceName to send: $deviceName")
        Log.d(TAG, "IP address to send: $ip")

        val ipInfoDto = IPInfoDto(deviceName ?: "", ip)
        val json = convertToJson(ipInfoDto, MessageType.IP_INFO)
        sendMessage(ownerHost, json)
    }

    fun sendLocationInfo(ownerHost: String) {
        val deviceName = this.deviceName
        val ip = getIpAddress()

        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                Log.d(TAG, "deviceName to send: $deviceName")
                Log.d(TAG, "IP address to send: $ip")

                CoroutineScope(Dispatchers.IO).launch {
                    val user = userInfoRepository.getUser() ?: return@launch
                    lastLocation?.let {
                        val locationDto = LocationDto(
                            username = user.username,
                            shipName = user.shipName,
                            description = user.description,
                            ipAddress = ip,
                            xCoordinate = it.latitude,
                            yCoordinate = it.longitude
                        )
                        val json = convertToJson(locationDto, MessageType.DEVICE_INFO)
                        sendMessage(ownerHost, json)
                    }
                }
            }
        }

        timer.schedule(task, 0, 5000)
        hostTimerMap[ownerHost] = timer
    }

    fun onDisconnectPeer() {
        val currentPeers = connectedDevices.map { it.ipAddress }
        val currentLocations = locationFlow.value
        val updatedLocations = currentLocations.filter { it.ipAddress in currentPeers }

        locationFlow.update {
            updatedLocations
        }
        stopHostTimer()
    }

    fun broadcastAddresses() {
        val broadcastDto = IPBroadcastDto(connectedDevices)
        val json = convertToJson(broadcastDto, MessageType.IP_BROADCAST)
        val deviceName = this.deviceName
        for (device in connectedDevices) {
            if (device.deviceName != deviceName) {
                sendMessage(device.ipAddress, json)
            }
        }
    }

    fun updateDevices(devices: List<IPInfoDto>) {
        connectedDevices = devices
    }

    fun getLocations() : Flow<List<LocationDto>> {
        return locationFlow.asStateFlow()
    }

    fun getOwnLocation() : Flow<ShipLocation?> {
        return ownLocationFlow.asStateFlow()
    }

    fun updateLocation(location: LocationDto) {
        val currentLocations = locationFlow.value
        if (location.ipAddress !in currentLocations.map { it.ipAddress }) {
            locationFlow.update {
                currentLocations + listOf(location)
            }
        } else {
            val newLocations = currentLocations.map {
                if (it.ipAddress == location.ipAddress) {
                    location
                } else {
                    it
                }
            }
            locationFlow.update {
                newLocations
            }
        }
    }

    fun getOwnIpInfo(): IPInfoDto {
        val deviceName = this.deviceName
        val ipAddr = getIpAddress()
        return IPInfoDto(deviceName ?: "", ipAddr)
    }

    private fun mockLocations() {
        var diff = 0
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                println("Task executed at: ${System.currentTimeMillis()}")
                locationFlow.update {
                    listOf(
                        LocationDto.mock().copy(
                            username = "Miś",
                            ipAddress = "10.11",
                            xCoordinate = 12.343 + diff,
                            yCoordinate = 12.442 + diff
                        ),
                        LocationDto.mock().copy(
                            username = "Miś 2",
                            ipAddress = "10.12",
                            xCoordinate = 9.343 + diff,
                            yCoordinate = 9.442 + diff
                        ),
                        LocationDto.mock().copy(
                            username = "Miś 3",
                            ipAddress = "10.13",
                            xCoordinate = 16.343 + diff,
                            yCoordinate = 24.442 + diff
                        )
                    )
                }
                diff++
            }
        }

        // Schedule the task to run every 5 seconds with an initial delay of 0 seconds
        timer.schedule(task, 0, 5000)
    }

    private fun getLocalIPAddress(): ByteArray? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        if (inetAddress is Inet4Address) {
                            return inetAddress.getAddress()
                        }
                    }
                }
            }
        } catch (ex: SocketException) {
            ex.message?.let { Log.e(TAG, it) };
        } catch (ex: NullPointerException) {
            ex.message?.let { Log.e(TAG, it) };
        }
        return null
    }

    private fun getDottedDecimalIP(ipAddr: ByteArray): String {
        var ipAddrStr = ""
        for (i in ipAddr.indices) {
            if (i > 0) {
                ipAddrStr += "."
            }
            ipAddrStr += ipAddr[i].toInt() and 0xFF
        }
        return ipAddrStr
    }

    private fun getIpAddress() : String {
        return getLocalIPAddress()?.let { getDottedDecimalIP(it) } ?: "UNKNOWN"
    }

    fun convertToJson(obj: Any, type: MessageType) : String {
        val nestedJson = gson.toJson(obj)
        val messageWrapper = MessageDto(type, nestedJson)
        return gson.toJson(messageWrapper)
    }

    private fun stopHostTimer() {
        val connectedDevicesIps = connectedDevices.map { it.ipAddress }
        val toStop = hostTimerMap.keys.filter { it !in connectedDevicesIps }
        toStop.forEach { key ->
            hostTimerMap[key]?.cancel()
            hostTimerMap.remove(key)
        }
    }

    fun getHostByUsername(username: String) : String? {
        return locationFlow.value.filter { it.username == username }.first().ipAddress
    }
}