package com.example.xml_app.activities

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.example.xml_app.viewModel.MapsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.Marker
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import kotlinx.coroutines.tasks.await

class MapsActivity : AppCompatActivity() {
    private val viewModel: MapsViewModel by viewModels()
    private var locationPermissionsGranted by mutableStateOf(false)
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true

        Log.d("MAPS", "Coarse : $coarseGranted")
        Log.d("MAPS", "FINE: $fineGranted")

        if (coarseGranted && fineGranted) {
            locationPermissionsGranted = true
        } else {
            finish()
        }
    }


    @OptIn(MapboxExperimental::class)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            true

        val permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        locationPermissionLauncher.launch(
            permissions.toTypedArray()
        )
        setContent {
            val location = remember {
                LocationServices.getFusedLocationProviderClient(this)
            }
            var userPoint by remember { mutableStateOf<Point?>(null) }
            val mapViewPortState = rememberMapViewportState()

            LaunchedEffect(locationPermissionsGranted) {
                if (locationPermissionsGranted) {
                    val result = location.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).await()
                    result?.let {
                        userPoint = Point.fromLngLat(
                            it.longitude,
                            it.latitude
                        )
                        Log.d(
                            "MAPS",
                            "Longitude: ${result.longitude} \n Latitude: ${result.latitude}"
                        )
                    }
                }
            }

            LaunchedEffect(userPoint) {
                userPoint?.let { point ->
                    mapViewPortState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .center(point)
                            .zoom(16.0)
                            .pitch(0.0)
                            .bearing(0.0)
                            .build(),
                        animationOptions = MapAnimationOptions.Builder()
                            .startDelay(100)
                            .duration(3000)
                            .build()
                    )
                }
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->
                MapboxMap(
                    modifier = Modifier.padding(innerPadding),
                    mapViewportState = mapViewPortState,
                ) {
                    userPoint?.let {
                        Marker(
                            point = it
                        )
                    }

                }

            }
        }
    }
}