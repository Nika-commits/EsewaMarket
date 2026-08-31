package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.BuildConfig
import com.example.xml_app.repository.MapRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.CreateUserRequest
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.CircularBounds
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.mapbox.geojson.Point
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.milliseconds

class MapsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val mapRepository = MapRepository()
    private val userRepository = UserRepository(app.database.userDao())
    private val placesClient: PlacesClient = Places.createClient(app)
    private val _userPoint = MutableStateFlow<Point?>(null)
    val userPoint = _userPoint.asStateFlow()
    private val _address = MutableStateFlow<String?>(null)
    val address = _address.asStateFlow()
    private val _isPointSelected = MutableStateFlow(false)
    val isPointSelected = _isPointSelected.asStateFlow()
    private val _isAddressLoading = MutableStateFlow(false)
    val isAddressLoading = _isAddressLoading.asStateFlow()
    private val _isUpdatingUser = MutableStateFlow(false)
    val isUpdatingUser = _isUpdatingUser.asStateFlow()
    private val _predictions = MutableStateFlow<List<AutocompletePrediction>>(emptyList())
    val predictions = _predictions.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()
    private var session: AutocompleteSessionToken? = null
    private var searchJob: Job? = null


    fun updateUserPoint(point: Point) {
        _userPoint.value = point
        getAddress(point)
    }

    fun toggleIsPointSelected(value: Boolean) {
        _isPointSelected.value = value
        if (_isPointSelected.value) {
            updateUserAddress()
        }
    }

    private fun getAddress(point: Point) {
        viewModelScope.launch {
            try {
                _isAddressLoading.value = true
                val response = mapRepository.getAddressFromCoordinates(
                    coordinates = "${point.longitude()},${point.latitude()}",
                    accessToken = BuildConfig.MapboxAccessToken
                )
                Log.d("MAPS", "$response.")
                val resolvedAddress = response.features.firstOrNull()?.place_name
                _address.value = resolvedAddress
                resolvedAddress?.let { _searchQuery.value = it }
                _predictions.value = emptyList()
                _isAddressLoading.value = false
            } catch (e: Exception) {
                Log.e("MAPS", e.message ?: "Error occurred while fetching location")
                _isAddressLoading.value = false
            }
        }
    }

    private fun updateUserAddress() {
        viewModelScope.launch {
            _isUpdatingUser.value = true
            try {
                val updateUserRequest = CreateUserRequest(
                    username = null,
                    fullName = null,
                    address = _address.value,
                    phone = null,
                    profilePicture = null
                )
                val firebaseUser = app.auth.currentUser ?: throw Exception("User is not logged in.")
                val token = firebaseUser.getIdToken(false).await().token
                    ?: throw Exception("Failed to get Firebase Token")
                val response = userRepository.updateUserProfile(
                    token = token,
                    request = updateUserRequest
                )

                if (!response.isSuccessful) {
                    Log.e("MAPS", "Failed to update: ${response.code()}")
                    throw Exception("Failed: ${response.code()}")
                }

                Log.d("MAPS", "Address : ${response.body()?.address}")
                _isUpdatingUser.value = false
            } catch (e: Exception) {
                Log.e("MAPS", "Exception : ${e.message}")
                _isUpdatingUser.value = false
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.isBlank()) {
            _predictions.value = emptyList()
            return
        }

        if (session == null) {
            session = AutocompleteSessionToken.newInstance()
        }

        searchJob = viewModelScope.launch {
            delay(300.milliseconds)
            try {
                _isSearching.value = true
                val requestBuilder = FindAutocompletePredictionsRequest.builder()
                    .setQuery(query)
                    .setSessionToken(session)

                _userPoint.value?.let { point ->
                    requestBuilder.setLocationBias(
                        CircularBounds.newInstance(
                            LatLng(point.latitude(), point.longitude()),
                            50_000.0
                        )
                    )
                }
                val response = placesClient
                    .findAutocompletePredictions(requestBuilder.build())
                    .await()
                _predictions.value = response.autocompletePredictions
            } catch (e: Exception) {
                Log.e("Maps", "Could not get AutoComplete ${e.message}")
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun selectPrediction(prediction: AutocompletePrediction) {
        _predictions.value = emptyList()
        _searchQuery.value = prediction.getFullText(null).toString()
        viewModelScope.launch {
            try {
                _isSearching.value = true
                val request = FetchPlaceRequest
                    .builder(
                        prediction.placeId, listOf(
                            Place.Field.LOCATION
                        )
                    )
                    .setSessionToken(session)
                    .build()

                val response = placesClient.fetchPlace(request).await()
                val location = response.place.location

                if (location != null) {
                    val point = Point.fromLngLat(location.longitude, location.latitude)
                    updateUserPoint(point)
                }
            } catch (e: Exception) {
                Log.e("Maps", "Could not select value ${e.message}")
            } finally {
                session = null
                _predictions.value = emptyList()
                _searchQuery.value = ""
                _isSearching.value = false
            }
        }
    }
}

