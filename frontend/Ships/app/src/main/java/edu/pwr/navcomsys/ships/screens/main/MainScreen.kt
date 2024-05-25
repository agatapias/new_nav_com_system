package edu.pwr.navcomsys.ships.screens.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import org.koin.androidx.compose.koinViewModel


@Composable
fun MainScreen() {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = MainUiInteraction.default(viewModel)
    MainScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction
    )
}

@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    uiInteraction: MainUiInteraction
) {
    val context = LocalContext.current
    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    MarkerPopUp(
        isVisible = uiState.isPopUpVisible,
        onClose = uiInteraction::onClosePopUp
    )
    Box {
        val map = rememberMapViewLifecycle()
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            factory = {
                map.apply {
                    Log.d("Map", "getMapAsync")
                    getMapAsync { mapboxMap ->
                        mapboxMap.setStyle(
                            Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")
                        ) {
                            Log.d("Map", "Map is ready")
                            uiInteraction.onMapLoaded()
                        }
                        mapboxMap.addMarker(
                            MarkerOptions()
                                .position(LatLng(12.33, 14.88))
                                .setTitle("Yes")
                                .setIcon(drawableToIcon(context, R.drawable.ic_my_boat))
//                                .setSnippet(markerSnippet)
                        )
                        mapboxMap.setOnMarkerClickListener { marker ->
                            Log.d("Map", "Marker clicked!")
                            uiInteraction.onShipClick(marker)
                            true
                        }
                    }
                }
            },
            update = {
            }
        )
        ShipsListFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimensions.space20),
            onClick = {}
        )
    }
}

fun drawableToIcon(context: Context, @DrawableRes id: Int, @SuppressLint("ResourceAsColor") @ColorInt colorRes: Int = R.color.white): Icon? {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, context.theme)
    val bitmap = Bitmap.createBitmap(
        55,
        120,
//        vectorDrawable!!.intrinsicWidth,
//        vectorDrawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    vectorDrawable!!.setBounds(0, 0, canvas.width, canvas.height)
    DrawableCompat.setTint(vectorDrawable, colorRes)
    vectorDrawable.draw(canvas)
    return IconFactory.getInstance(context).fromBitmap(bitmap)
}

@Composable
fun ShipsListFloatingButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(modifier = modifier) {
        FloatingActionButton(
            modifier = Modifier.size(60.dp),
            onClick = { onClick() },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.surface,
            shape = CircleShape,
        ) {
            Image(
                modifier = Modifier.size(Dimensions.space30),
                painter = painterResource(id = edu.pwr.navcomsys.ships.R.drawable.ic_list),
                colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.surface),
                contentDescription = "Action button for list of ships."
            )
        }
    }
}