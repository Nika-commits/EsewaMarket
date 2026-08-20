package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.R
import com.example.xml_app.repository.MapboxRepository
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val mapboxRepository = MapboxRepository()
    private val _userPoint = MutableStateFlow<Point?>(null)
    val userPoint = _userPoint.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address = _address.asStateFlow()

    private val _isPointSelected = MutableStateFlow(false)
    val isPointSelected = _isPointSelected.asStateFlow()
    private val _isAddressLoading = MutableStateFlow(false)
    val isAddressLoading = _isAddressLoading.asStateFlow()

    fun updateUserPoint(point: Point) {
        _userPoint.value = point
        getAddress(point)
    }

    fun toggleIsPointSelected(value: Boolean) {
        _isPointSelected.value = value
    }

    private fun getAddress(point: Point) {
        viewModelScope.launch {
            try {
                _isAddressLoading.value = true
                val accessToken = getApplication<Application>()
                    .getString(R.string.mapbox_access_token)
                val response = mapboxRepository.getAddressFromCoordinates(
                    coordinates = "${point.longitude()},${point.latitude()}",
                    accessToken = accessToken
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
}
