package edu.pwr.navcomsys.ships

import WiFiDirectBroadcastReceiver
import WiFiDirectBroadcastReceiver.Companion.discoverPeers
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import edu.pwr.navcomsys.ships.model.backgroundservice.UserInfoService
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.wifidirect.MessageListener
import edu.pwr.navcomsys.ships.screens.AppViewModel
import edu.pwr.navcomsys.ships.ui.navigation.BottomNavigationBar
import edu.pwr.navcomsys.ships.ui.navigation.Screen
import edu.pwr.navcomsys.ships.ui.navigation.appendArguments
import edu.pwr.navcomsys.ships.ui.theme.ShipsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity(){
    private var hasNewMessage: MutableState<Boolean> = mutableStateOf(false)
    private val peerRepository: PeerRepository by inject()
    private val messageListener: MessageListener by inject()
    private lateinit var channel: WifiP2pManager.Channel
    private lateinit var manager: WifiP2pManager
    private lateinit var receiver: BroadcastReceiver
    private val intentFilter = IntentFilter().apply {
        addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
    }

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

        manager = getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
        channel = manager.initialize(this, mainLooper, null)

        Log.d("Main", BuildConfig.VERSION_CODE.toString())
        // Check if location permissions are granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.NEARBY_WIFI_DEVICES),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Start the service if permissions are already granted
            startLocationService()
            receiver = WiFiDirectBroadcastReceiver(manager, channel, this, peerRepository)

            registerReceiver(receiver, intentFilter)
            messageListener.startListening()
        }
    }

    override fun onResume() {
        Log.d("Main", "onResume called")
        super.onResume()
        registerReceiver(receiver, intentFilter)
        val activity = this
        CoroutineScope(Dispatchers.Default).launch {
            discoverPeers(manager, channel, activity)
        }
    }

    override fun onPause() {
        Log.d("Main", "onPause called")
        super.onPause()
        unregisterReceiver(receiver)
    }

    override fun onStop() {
        super.onStop()
        try {
            manager.removeGroup(channel, null)
        } catch (e: Exception) {
            Log.e("Main", "exception when closing app", e)
        }
    }

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
}

@Composable
fun AppContent(hasNewMessage: Boolean = false) {
    val navController = rememberNavController()
    val viewModel: AppViewModel = koinViewModel()

    LaunchedEffect(Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.navigationEvent.collectLatest { ip ->
                ip?.let {
                    navController.navigate(Screen.Call.path.appendArguments(ip+"|I"))
                }
            }
        }
    }

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