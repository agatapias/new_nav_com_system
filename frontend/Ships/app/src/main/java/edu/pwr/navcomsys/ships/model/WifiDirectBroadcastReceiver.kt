import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.util.Log
import androidx.core.app.ActivityCompat
import edu.pwr.navcomsys.ships.MainActivity.Companion.LOCATION_PERMISSION_REQUEST_CODE
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
    private val activity: Activity
) : BroadcastReceiver() {
    private var lastState: Int? = null

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

                        CoroutineScope(Dispatchers.Default).launch {
                            discoverPeers(manager, channel, activity)
                        }
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
                // Respond to new connection or disconnections
            }
            WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION -> {
                // Respond to this device's wifi state changing
            }
        }
    }

    private fun requestPeers() {
        Log.d(TAG, "requestPeers called")
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            manager.requestPeers(channel) { peers: WifiP2pDeviceList? ->
                for (peer in peers?.deviceList!!) {
                    connectToDevice(peer)
                }
            }
        }
    }

    private fun connectToDevice(device: WifiP2pDevice) {
        Log.d(TAG, "connectToDevice called")
        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.NEARBY_WIFI_DEVICES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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
                }
            })
        }
    }

    companion object {
        fun discoverPeers(
            manager: WifiP2pManager,
            channel: WifiP2pManager.Channel,
            activity: Activity
        ) {
            Log.d(TAG, "discoverPeers called")
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.NEARBY_WIFI_DEVICES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
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