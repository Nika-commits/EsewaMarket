package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class MapboxRepository {

    suspend fun getAddressFromCoordinates(
        coordinates: String,
        accessToken: String
    ) = RetrofitInstance.addressApi.getAddressFromCoordinates(
        coordinates = coordinates,
        accessToken = accessToken
    )
}