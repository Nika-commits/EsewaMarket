package com.example.xml_app.utils.dto.request

data class CreateOrderRequest(
    val address: String,
    val phone: String,
    val paymentOption: PaymentOptions,
    val promocode: String?,
    val items: List<CreateOrderItemRequest>
)

data class CreateOrderItemRequest(
    val productId: Int,
    val quantity: Int
)

enum class PaymentOptions {
    Cash_On_Delivery, Esewa
}


data class UpdateOrderStatusRequest(
    val status: OrderStatus
)

enum class OrderStatus {
    Initialized, Pending, Shipped, Delivered, Cancelled
}

enum class PaymentStatus {
    Pending, Paid, Refund
}