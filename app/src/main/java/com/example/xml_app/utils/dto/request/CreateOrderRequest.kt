package com.example.xml_app.utils.dto.request

data class CreateOrderRequest(
    val address: String,
    val phone: String,
    val paymentOptions: PaymentOptions,
    val promocode: String,
    val items: List<CreateOrderItemRequest>
)

data class CreateOrderItemRequest(
    val productId: Int,
    val quantity: Int
)

enum class PaymentOptions {
    Cash_On_Delivery, Esewa
}