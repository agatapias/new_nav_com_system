
import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.pwr.navcomsys.ships.MainActivity.Companion.LOCATION_PERMISSION_REQUEST_CODE
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "WifiDirect"

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
class WiFiDirectBroadcastReceiver(
    private val manager: WifiP2pManager,
    private val channel: WifiP2pManager.Channel,
    private val activity: Activity,
    private val peerRepository: PeerRepository
) : BroadcastReceiver() {
    private var lastState: Int? = null
    private var connected: MutableList<String> = mutableListOf()
    private val name: String = android.os.Build.MODEL

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "WiFiDirectBroadcastReceiver called")
        val action: String? = intent.action
        Log.d(TAG, "action: $action")
        when (action) {
            WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION -> {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)

                if (lastState == state) return
                lastState = state
                when (state) {
                    WifiP2pManager.WIFI_P2P_STATE_ENABLED -> {
                        Log.d(TAG, "wifi p2p state enabled")
                        // Wifi P2P is enabled

//                        CoroutineScope(Dispatchers.Default).launch {
//                            discoverPeers(manager, channel, activity)
//                        }
                    }
                    else -> {
                        Log.d(TAG, "wifi p2p state disabled")
                        // Wi-Fi P2P is not enabled
                    }
                }
            }
            WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                // Call WifiP2pManager.requestPeers() to get a list of current peers
                requestPeers()
            }
            WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                Log.d(TAG, "STATUS: wifi p2p new connection sth")
                // Respond to new connection or disconnections
                handleConnectionChanged(intent)
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }

    private fun requestPeers() {
        Log.d(TAG, "requestPeers called")
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                for (peer in peers?.deviceList!!) {
                    Log.d(TAG, "found peer: ${peer.deviceName}")
                    Log.d(TAG, "found peer ad: ${peer.deviceAddress}")
                    if (peer.deviceName != "DIRECT-6C-HP Smart Tank 580-590" && !connected.contains(peer.deviceName)) {
                        CoroutineScope(Dispatchers.IO).launch {
                            connectToDevice(peer)
                        }
                    }
                }
            }
        }
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        Log.d(TAG, "connectToDevice called")
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            manager.connect(channel, config, object : WifiP2pManager.ActionListener {
                override fun onSuccess() {
                    Log.d(TAG, "Successfully connected to device! ${device.deviceName}")
                }

                override fun onFailure(reason: Int) {
                    Log.d(TAG, "Failed to connect to device: ${device.deviceName}")
                    Log.d(TAG, "Fail, reason: $reason")
//                    connectToDevice(device)
                }
            })
        }
    }

    private fun handleConnectionChanged(intent: Intent) {
        Log.d(TAG, "handleConnectionChanged called")
        val networkInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
        val device: WifiP2pDevice? = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
        peerRepository.deviceName = device?.deviceName

        if (networkInfo != null && networkInfo.isConnected) {
            // We are connected, request connection info to find out which device connected
            manager.requestConnectionInfo(channel, WifiP2pManager.ConnectionInfoListener { info ->
                Log.d(TAG, "Connected to group owner: ${info.groupOwnerAddress}")
                if (info.groupFormed && info.isGroupOwner) {
                    Log.d(TAG, "This device is the group owner")
                    // Handle group owner logic
                } else if (info.groupFormed) {
                    Log.d(TAG, "This device is a client")
                    // Handle client logic
                }
                requestGroupInfo()

                if (info.isGroupOwner) {
                    val newConnectedDevices = peerRepository.connectedDevices.filter {
                        it.deviceName in connected
                    }
                    peerRepository.connectedDevices = newConnectedDevices
                } else {
                    info.groupOwnerAddress.hostAddress?.let { peerRepository.sendIPInfo(it) }
                    info.groupOwnerAddress.hostAddress?.let { peerRepository.sendLocationInfo(it) }
                }
            })
        } else {
            // We are disconnected
            Log.d(TAG, "Disconnected from peer")
            // TODO: stop sending location to host
            onDisconnected()
        }
    }

    private fun requestGroupInfo() {
        Log.d(TAG, "requestGroupInfo called")
        if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "discoverPeers missing permissions")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            manager.requestGroupInfo(channel) { group ->

                if (group != null) {
                    val users = group.clientList + group.owner
                    Log.d(TAG, group.clientList.toString())
                    Log.d(TAG, "Group info available")
                    Log.d(TAG, "Self device name: $name")
                    connected.clear()
                    connected.addAll(users.map { it.deviceName })
                }
            }
        }
    }

    private fun onDisconnected() {
        Log.d(TAG, "onDisconnected called")
        // Handle disconnection logic
        discoverPeers(manager, channel, activity)
    }


    companion object {
        fun discoverPeers(
            manager: WifiP2pManager,
            channel: WifiP2pManager.Channel,
            activity: Activity
        ) {
            Log.d(TAG, "discoverPeers called")
            if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "discoverPeers missing permissions")
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.NEARBY_WIFI_DEVICES
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                Log.d(TAG, "manager discoverPeers called")
                manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Log.d(TAG, "Discovery successful.")
                    }

                    override fun onFailure(reasonCode: Int) {
                        Log.d(TAG, "Discovery failed.")
                    }
                })
            }
        }
    }
}