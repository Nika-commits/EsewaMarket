package com.example.xml_app.utils.dto.response

import com.example.xml_app.utils.dto.request.PaymentOptions

data class OrderResponse(
    val id: Int,
    val address: String,
    val phone: String,
    val paymentOptions: PaymentOptions,
    val vehicleNumber: String,
    val deliveryCharge: Int,
    val discount: Int,
    val status: String,
    val totalPrice: Int,
    val orderItems: List<OrderItemResponse>
)

data class OrderItemResponse(
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val price: Int
)