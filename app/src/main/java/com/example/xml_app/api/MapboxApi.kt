package com.example.xml_app.api

import com.example.xml_app.utils.dto.AddressResponseMapbox
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MapboxApi {

    @GET("geocoding/v5/mapbox.places/{coordinates}.json")
    suspend fun getAddressFromCoordinates(
        @Path("coordinates") coordinates: String,
        @Query("limit") limit: Int = 1,
        @Query("access_token") accessToken: String
    ): AddressResponseMapbox
}