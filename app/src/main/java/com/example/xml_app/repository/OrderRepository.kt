package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.dto.request.CreateOrderRequest

class OrderRepository {

    suspend fun postOrder(
        token: String,
        request: CreateOrderRequest
    ) = RetrofitInstance.orderApi.postOrder(
        token,
        request
    )

    suspend fun getOrderById(
        id: Int,
        token: String
    ) = RetrofitInstance.orderApi.getOrderById(
        id,
        token
    )

    suspend fun getOrders(
        token: String
    ) = RetrofitInstance.orderApi.getOrders(
        token
    )
}