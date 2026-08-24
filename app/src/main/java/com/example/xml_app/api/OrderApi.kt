package com.example.xml_app.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface OrderApi {

    @POST("api/order")
    suspend fun postOrder(
        @Header("Authorization") authorization: String,
        @Body request
    )
}