package com.example.xml_app.utils.dto.response


data class KhaltiPaymentResponse(
    val pidx: String,
    val paymentUrl: String,
    val expiresAt: String,
    val expiresIn: Int
)

data class KhaltiPaymentVerificationResponse(
    val pidx: String,
    val totalAmount: Int,
    val status: String,
    val transactionId: String,
    val fee: Int,
    val refunded: Boolean
)