package edu.pwr.navcomsys.ships

import android.os.Bundle
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
import edu.pwr.navcomsys.ships.ui.navigation.BottomNavigationBar
import edu.pwr.navcomsys.ships.ui.theme.ShipsTheme

class MainActivity : ComponentActivity() {
    private var hasNewMessage: MutableState<Boolean> = mutableStateOf(false)

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