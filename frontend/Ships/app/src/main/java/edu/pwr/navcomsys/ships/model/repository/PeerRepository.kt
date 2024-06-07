package edu.pwr.navcomsys.ships.model.repository

import android.util.Log
import com.google.gson.Gson
import edu.pwr.navcomsys.ships.data.dto.IPBroadcastDto
import edu.pwr.navcomsys.ships.data.dto.IPInfoDto
import edu.pwr.navcomsys.ships.data.dto.LocationDto
import edu.pwr.navcomsys.ships.data.dto.MessageDto
import edu.pwr.navcomsys.ships.data.enums.MessageType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.SocketException


private const val TAG = "PeerRepository"

class PeerRepository {
    var connectedDevices: List<IPInfoDto> = mutableListOf()
    val locationFlow: MutableStateFlow<List<LocationDto>> = MutableStateFlow(emptyList())
    private val scope = CoroutineScope(Dispatchers.IO)
    private val gson = Gson()
    var deviceName: String? = null

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

    fun updateLocation(location: LocationDto) {
        // TODO: implement
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

    private fun convertToJson(obj: Any, type: MessageType) : String {
        val nestedJson = gson.toJson(obj)
        val messageWrapper = MessageDto(type, nestedJson)
        return gson.toJson(messageWrapper)
    }

    fun getOwnIpInfo(): IPInfoDto {
        val deviceName = this.deviceName
        val ipAddr = getIpAddress()
        return IPInfoDto(deviceName ?: "", ipAddr)
    }
}