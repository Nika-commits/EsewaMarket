package com.example.xml_app.utils.dto.response

data class OrderResponse(
    val id: Int,
    val address: String,
    val phone: String,
    val paymentOption: String,
    val vehicleNumber: String,
    val deliveryCharge: Int,
    val discount: Int,
    val status: String,
    val totalPrice: Int,
    val orderDate: String,
    val orderItems: List<OrderItemResponse>
)

data class OrderItemResponse(
    val productId: Int,
    val productName: String,
    val productImage: String,
    val brand: String,
    val quantity: Int,
    val price: Int
)