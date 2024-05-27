package edu.pwr.navcomsys.ships

import WiFiDirectBroadcastReceiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import edu.pwr.navcomsys.ships.model.backgroundservice.UserInfoService
import edu.pwr.navcomsys.ships.ui.navigation.BottomNavigationBar
import edu.pwr.navcomsys.ships.ui.theme.ShipsTheme


class MainActivity : ComponentActivity(), WifiP2pManager.PeerListListener {
    private var hasNewMessage: MutableState<Boolean> = mutableStateOf(false)

    private val intentFilter = IntentFilter()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var receiver: BroadcastReceiver

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShipsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(hasNewMessage.value)
                }
            }
        }

        // Check if location permissions are granted
//        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // Request location permissions
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // Start the service if permissions are already granted
//            startLocationService()
//        }

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        receiver = WiFiDirectBroadcastReceiver(manager, channel, this)

        registerReceiver(receiver, intentFilter)

//        val activity = this
//        CoroutineScope(Dispatchers.Default).launch {
//            discoverPeers(manager, channel, activity)
//        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(receiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    // delete below

    private fun startLocationService() {
        val intent = Intent(this, UserInfoService::class.java)
        startService(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, start the service
                startLocationService()
            } else {
                // Permission denied, handle accordingly
                // You can show a message to the user explaining why the permission is necessary
                showPermissionDeniedMessage()
            }
        }
    }

    private fun showPermissionDeniedMessage() {
        // Show a message to the user explaining why the permission is necessary
        Toast.makeText(this, "Location permission is required to use this feature", Toast.LENGTH_LONG).show()
    }

    override fun onPeersAvailable(wifiP2pDeviceList: WifiP2pDeviceList?) {
        Log.d("WifiDirect", "onPeersAvailable called")
        if (!wifiP2pDeviceList?.deviceList.isNullOrEmpty()) {
            val deviceList = wifiP2pDeviceList?.deviceList?.toList()
            Log.d("WifiDirect", "device list:")
            deviceList?.forEach {
                Log.d("WifiDirect", it.deviceName)
            }
//            for (device in deviceList) {
//                deviceNames.add(device.deviceName)
//            }
        }
    }
}

@Composable
fun AppContent(hasNewMessage: Boolean = false) {
    val navController = rememberNavController()
//    val viewModel: MainViewModel = koinViewModel()
//    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
//    viewModel.toast.CollectToast(context)

    AnimatedVisibility(visible = true) {
        BottomNavigationBar(
            navController = navController,
            showMessageDot = hasNewMessage
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ShipsTheme {
        AppContent()
    }
}