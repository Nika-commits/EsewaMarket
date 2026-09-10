package com.example.xml_app.utils.dto.response

data class EsewaPaymentResponse(
    val productId: String,
    val productName: String,
    val totalAmount: String,
    val code: String,
    val message: Message,
    val transactionDetails: TransactionDetails,
    val merchantName: String
)

data class Message(
    val technicalSuccessMessage: String,
    val successMessage: String
)

data class TransactionDetails(
    val date: String,
    val referenceId: String,
    val status: String
)