package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.dto.request.CreateOrderRequest

class OrderRepository {

    suspend fun postOrder(
        token: String,
        request: CreateOrderRequest
    ) = RetrofitInstance.orderApi.postOrder(
        "Bearer $token",
        request
    )

    suspend fun getOrderById(
        id: Int,
        token: String
    ) = RetrofitInstance.orderApi.getOrderById(
        id,
        "Bearer $token"
    )

    suspend fun getOrders(
        token: String
    ) = RetrofitInstance.orderApi.getOrders(
        "Bearer $token"
    )
}