package com.example.xml_app.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.xml_app.BuildConfig
import com.example.xml_app.R
import com.example.xml_app.utils.SourceSansPro
import com.example.xml_app.utils.styles.PrimaryGreen
import com.example.xml_app.utils.styles.PrimaryGreenTransparent
import com.example.xml_app.utils.styles.Surface
import com.example.xml_app.utils.styles.components.AppButton
import com.example.xml_app.utils.styles.components.AppTextField
import com.example.xml_app.utils.styles.components.ButtonVariant
import com.example.xml_app.viewModel.MapsViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.libraries.places.api.Places
import com.mapbox.common.MapboxOptions
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
    companion object{
        const val EXTRA_ADDRESS = "extra_address"
        const val EXTRA_LATITUDE = "extra_latitude"
        const val EXTRA_LONGITUDE = "extra_longitude"
    }
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

        MapboxOptions.accessToken = BuildConfig.MapboxAccessToken
        val apiKey = BuildConfig.PLACES_API_KEY
        if (apiKey.isEmpty()) {
            Log.e("Maps", "No Places Api Key")
            finish()
            return
        }
        Log.d("Maps", apiKey)
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

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
            val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
            val predictions by viewModel.predictions.collectAsStateWithLifecycle()
            val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()
            val isAddressSearching by viewModel.isAddressLoading.collectAsStateWithLifecycle()
            val userPoint by viewModel.userPoint.collectAsStateWithLifecycle()
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

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(24.dp)
                    ) {

                        SearchBox(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Surface),
                            isLoading = isAddressSearching || isSearching,
                            onClear = {
                                viewModel.onSearchQueryChange("")
                            }
                        )

                        if (predictions.isNotEmpty()) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Surface)
                            ) {
                                predictions.forEach { prediction ->
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.selectPrediction(prediction) }
                                            .padding(horizontal = 16.dp, vertical = 10.dp)
                                    ) {
                                        Text(
                                            prediction.getPrimaryText(null).toString(),
                                            fontFamily = SourceSansPro
                                        )

                                        Text(
                                            prediction.getSecondaryText(null).toString(),
                                            fontFamily = SourceSansPro
                                        )
                                    }
                                }
                            }
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
                                val address = viewModel.address.value
                                val point = viewModel.userPoint.value

                                if(address != null && point != null){
                                    val resultIntent = Intent().apply {
                                        putExtra(EXTRA_ADDRESS, address)
                                        putExtra(EXTRA_LATITUDE, point.latitude())
                                        putExtra(EXTRA_LONGITUDE, point.longitude())
                                    }
                                    setResult(RESULT_OK, resultIntent)
                                    finish()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isLoading: Boolean,
    onClear: () -> Unit
) {
    AppTextField(
        modifier,
        value = value,
        onValueChange = onValueChange,
        placeholder = "Enter your address",
        isLoading = isLoading,
        startButton = {
            Icon(
                modifier = Modifier
                    .clickable(
                        enabled = true,
                        onClick = onClear
                    ),
                painter = painterResource(R.drawable.ic_cancel),
                contentDescription = null
            )
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SearchBoxPreview() {
    SearchBox(
        value = "",
        onValueChange = {
        },
        isLoading = true,
        onClear = {},
    )
}