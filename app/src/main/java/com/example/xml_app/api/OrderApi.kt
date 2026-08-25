package com.example.xml_app.api

import com.example.xml_app.utils.dto.request.CreateOrderRequest
import com.example.xml_app.utils.dto.response.OrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderApi {

    @POST("/api/order")
    suspend fun postOrder(
        @Header("Authorization") authorization: String,
        @Body request: CreateOrderRequest
    ): Response<OrderResponse>

    @GET("/api/order/{id}")
    suspend fun getOrderById(
        @Path("id") id: Int,
        @Header("Authorization") authorization: String
    ): Response<OrderResponse>

    @GET("/api/order")
    suspend fun getOrders(
        @Header("Authorization") authorization: String
    ): Response<List<OrderResponse>>
}