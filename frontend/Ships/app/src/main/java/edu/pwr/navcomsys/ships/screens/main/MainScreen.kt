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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import edu.pwr.navcomsys.ships.R
import edu.pwr.navcomsys.ships.ui.component.Loader
import edu.pwr.navcomsys.ships.ui.component.ShipData.Companion.toShipData
import edu.pwr.navcomsys.ships.ui.component.ShipMarkerPopUp
import edu.pwr.navcomsys.ships.ui.theme.Dimensions
import org.koin.androidx.compose.koinViewModel

private const val YOU_KEY = "you"

@Composable
fun MainScreen(
    navigation: MainNavigation
) {
    val viewModel: MainViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val uiInteraction = MainUiInteraction.default(viewModel)
    MainScreenContent(
        uiState = uiState,
        uiInteraction = uiInteraction,
        navigation = navigation
    )
}

@SuppressLint("ResourceAsColor")
@Composable
private fun MainScreenContent(
    uiState: MainUiState,
    uiInteraction: MainUiInteraction,
    navigation: MainNavigation
) {
    val context = LocalContext.current
    if (uiState.isLoading) {
        Loader(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
    }
    ShipMarkerPopUp(
        isVisible = uiState.isPopUpVisible,
        ship = uiState.ship?.toShipData(),
        onClose = uiInteraction::onClosePopUp
    )
    Box {
        val map = rememberMapViewLifecycle()
        var mapboxMapState: MapboxMap? by remember { mutableStateOf(null) }
        var markers: Map<String, Marker> by remember { mutableStateOf(emptyMap()) }

        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            factory = {
                map.apply {
                    Log.d("Map", "getMapAsync")
                    getMapAsync { mapboxMap ->
                        mapboxMapState = mapboxMap
                        mapboxMap.setStyle(
                            Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")
                        ) {
                            Log.d("Map", "Map is ready")
                            uiInteraction.onMapLoaded()
                        }
                    }
                }
            },
            update = {
                map.apply {
                    Log.d("Map", "map in update called")
                    Log.d("Map", "markers: $markers")
                    uiState.yourLocation?.let { loc ->
                        if (markers.containsKey(YOU_KEY)) {
                            val currentMarker = markers[YOU_KEY]
                            currentMarker?.position = LatLng(loc.lat, loc.lng)
                            currentMarker?.let {
                                val modifiedMarkers = markers.toMutableMap()
                                modifiedMarkers[YOU_KEY] = currentMarker
                                markers = modifiedMarkers
                            }
                        } else {
                            val newMarker = mapboxMapState?.addMarker(
                                MarkerOptions()
                                    .position(LatLng(loc.lat, loc.lng))
                                    .setTitle("Yes")
                                    .setIcon(drawableToIcon(context, R.drawable.ic_my_boat, R.color.teal_700))
                            )
                            newMarker?.let { m ->
                                markers = markers + (YOU_KEY to m)
                            }
                        }
                    }
                    uiState.shipLocations.forEach { ship ->
                        if (markers.containsKey(ship.ipAddress)) {
                            val currentMarker = markers[ship.ipAddress]
                            currentMarker?.position = LatLng(ship.xCoordinate, ship.yCoordinate)
                            currentMarker?.let {
                                Log.d("Map", "updated marker: ${ship.username}")
                                val modifiedMarkers = markers.toMutableMap()
                                modifiedMarkers[ship.ipAddress] = currentMarker
                                markers = modifiedMarkers
                            }
                        } else {
                            Log.d("Map", "added marker: ${ship.username}")
                            val newMarker = mapboxMapState?.addMarker(
                                MarkerOptions()
                                    .position(LatLng(ship.xCoordinate, ship.yCoordinate))
                                    .setTitle("Yes")
                                    .setIcon(drawableToIcon(context, R.drawable.ic_my_boat))
                            )
                            newMarker?.let { m ->
                                markers = markers + (ship.ipAddress to m)
                            }
                        }
                    }
                    val locationIds = uiState.shipLocations.map { it.ipAddress }
                    val toRemove = markers.filter { it.key !in locationIds && it.key != YOU_KEY}
                    toRemove.forEach {
                        Log.d("Map", "removed marker: ${it.key}, ${uiState.shipLocations.firstOrNull { s -> s.ipAddress == it.key }}")
                        mapboxMapState?.removeMarker(it.value)
                    }
                    mapboxMapState?.setOnMarkerClickListener { marker ->
                        Log.d("Map", "Marker clicked!")
                        val shipKey = markers.filterValues { it == marker }.keys.first()
                        val ship = uiState.shipLocations.firstOrNull { it.ipAddress == shipKey }
                        Log.d("Map", "shipLocations: ${uiState.shipLocations}")

                        Log.d("Map", "ship ip address: ${ship?.ipAddress}, ship key: $shipKey")
                        ship?.let {
                            uiInteraction.onShipClick(it)
                        }
                        true
                    }
                }
            }
        )
        ShipsListFloatingButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(Dimensions.space20),
            onClick = navigation::openShipList
        )
    }
}

fun drawableToIcon(context: Context, @DrawableRes id: Int, @SuppressLint("ResourceAsColor") @ColorInt colorRes: Int = R.color.white): Icon? {
    val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, context.theme)
    val bitmap = Bitmap.createBitmap(
        25,
        60,
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
                colorFilter = ColorFilter.tint(color = Color.White),
                contentDescription = "Action button for list of ships."
            )
        }
    }
}