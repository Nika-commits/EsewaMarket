package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

class MapRepository {

    suspend fun getAddressFromCoordinates(
        coordinates: String,
        accessToken: String
    ) = run {
        delay(3000.milliseconds)
        RetrofitInstance.addressApi.getAddressFromCoordinates(
            coordinates = coordinates,
            accessToken = accessToken
        )
    }
}