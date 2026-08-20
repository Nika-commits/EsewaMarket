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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.TextDark300
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppLoadingIndicator
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.MapsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.ViewAnnotationAnchor
import com.mapbox.maps.ViewAnnotationOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.viewannotation.annotationAnchor
import com.mapbox.maps.viewannotation.geometry
import kotlinx.coroutines.launch
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
            val userPoint by viewModel.userPoint.collectAsStateWithLifecycle()
            val currentAddress by viewModel.address.collectAsStateWithLifecycle()
            val isAddressLoading by viewModel.isAddressLoading.collectAsStateWithLifecycle()
            val isPointSelected by viewModel.isPointSelected.collectAsStateWithLifecycle()
            val isUpdatingUser by viewModel.isUpdatingUser.collectAsStateWithLifecycle()
            val mapViewPortState = rememberMapViewportState()
            val scope = rememberCoroutineScope()

            LaunchedEffect(locationPermissionsGranted) {
                if (locationPermissionsGranted) {
                    val result = location.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        CancellationTokenSource().token
                    ).await()
                    result?.let {
                        val newUserPoint = Point.fromLngLat(
                            it.longitude,
                            it.latitude
                        )
                        viewModel.updateUserPoint(newUserPoint)
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
                            .duration(2000)
                            .build()
                    )
                }
            }
            Scaffold(
                modifier = Modifier
                    .fillMaxSize(),
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
            ) { innerPadding ->

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {

                    MapboxMap(
                        modifier = Modifier.fillMaxSize(),
                        mapViewportState = mapViewPortState,
                        scaleBar = {

                        },
                        compass = {

                        },
                        onMapClickListener = { point ->
                            viewModel.updateUserPoint(point)
                            viewModel.toggleIsPointSelected(false)
                            true
                        }
                    ) {
                        userPoint?.let {
                            CircleAnnotation(it) {
                                circleRadius = 8.0
                                circleColor = PrimaryGreen
                                circleStrokeWidth = 6.0
                                circleStrokeColor = PrimaryGreenTransparent
                            }
                            ViewAnnotation(
                                options = ViewAnnotationOptions
                                    .Builder()
                                    .geometry(it)
                                    .annotationAnchor {
                                        anchor(ViewAnnotationAnchor.BOTTOM)
                                        offsetY(40.0)
                                    }
                                    .build()
                            ) {
                                AppButton(
                                    variant = if (isPointSelected) ButtonVariant.PRIMARY else ButtonVariant.SECONDARY,
                                    text = if (isPointSelected) "Selected" else "Select",
                                    onClick = { viewModel.toggleIsPointSelected(true) },
                                    isLoading = isUpdatingUser
                                )
                            }
                        }

                    }

                    Row(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .statusBarsPadding()
                            .padding(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Surface)
                            .padding(vertical = 6.dp, horizontal = 2.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = if (isAddressLoading) Arrangement.SpaceBetween else Arrangement.spacedBy(
                            8.dp
                        )
                    ) {
                        AppButton(
                            variant = ButtonVariant.GHOST,
                            icon = R.drawable.ic_cancel,
                            onClick = {}
                        )
                        if (isAddressLoading) {
                            AppLoadingIndicator(
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        } else {
                            Text(
                                modifier = Modifier.padding(vertical = 4.dp),
                                text = currentAddress ?: "",
                                fontFamily = SourceSansPro,
                                color = TextDark300,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        AppButton(
                            variant = ButtonVariant.OUTLINE,
                            icon = R.drawable.ic_gps,
                            tint = PrimaryGreen,
                            onClick = {
                                scope.launch {
                                    if (locationPermissionsGranted) {
                                        val result = location.getCurrentLocation(
                                            Priority.PRIORITY_HIGH_ACCURACY,
                                            CancellationTokenSource().token
                                        ).await()

                                        result?.let {
                                            val newUserPoint = Point.fromLngLat(
                                                it.longitude,
                                                it.latitude
                                            )
                                            viewModel.updateUserPoint(newUserPoint)
                                        }
                                    }
                                }
                            }
                        )

                        AppButton(
                            variant = ButtonVariant.PRIMARY,
                            text = "DONE",
                            onClick = {
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }
}