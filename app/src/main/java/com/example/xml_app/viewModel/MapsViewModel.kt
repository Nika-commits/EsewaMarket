package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.BuildConfig
import com.example.xml_app.repository.MapboxRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.CreateUserRequest
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val mapboxRepository = MapboxRepository()
    private val userRepository = UserRepository(app.database.userDao())
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
//                val accessToken = getApplication<Application>()
//                    .getString(R.string.mapbox_access_token)
                val response = mapboxRepository.getAddressFromCoordinates(
                    coordinates = "${point.longitude()},${point.latitude()}",
                    accessToken = BuildConfig.MapboxAccessToken
                )
                Log.d("MAPS", "$response.")
                _address.value = response.features.firstOrNull()?.place_name
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
}
