package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.dto.request.CreateOrderRequest
import com.example.xml_app.utils.dto.request.UpdateOrderStatusRequest

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
        token: String,
        status: String
    ) = RetrofitInstance.orderApi.getOrders(
        "Bearer $token",
        status
    )

    suspend fun updateOrderStatus(
        id: Int,
        token: String,
        request: UpdateOrderStatusRequest
    ) = RetrofitInstance.orderApi.updateOrderStatus(
        id = id,
        authorization = "Bearer $token",
        request = request
    )
}