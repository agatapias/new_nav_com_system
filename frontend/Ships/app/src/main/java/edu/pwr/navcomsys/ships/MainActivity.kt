package edu.pwr.navcomsys.ships

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import edu.pwr.navcomsys.ships.model.backgroundservice.UserInfoService
import edu.pwr.navcomsys.ships.ui.navigation.BottomNavigationBar
import edu.pwr.navcomsys.ships.ui.theme.ShipsTheme

class MainActivity : ComponentActivity() {
    private var hasNewMessage: MutableState<Boolean> = mutableStateOf(false)

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
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
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Start the service if permissions are already granted
            startLocationService()
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