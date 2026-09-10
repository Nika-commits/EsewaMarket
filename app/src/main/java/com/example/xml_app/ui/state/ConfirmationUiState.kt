package com.example.xml_app.ui.state

import com.example.xml_app.utils.dto.request.PaymentOptions
import com.example.xml_app.utils.dto.response.OrderResponse

sealed interface ConfirmationUiState {
    data object Loading : ConfirmationUiState
    data class Success(
        val order: OrderResponse
    ) : ConfirmationUiState

    data object Error : ConfirmationUiState
}

sealed interface PaymentState {
    data object Idle : PaymentState
    data class Loading(
        val method: PaymentOptions
    ) : PaymentState

    data class Verifying(
        val method: PaymentOptions
    ) : PaymentState

    data class Error(
        val method: PaymentOptions
    ) : PaymentState

    data class Success(
        val order: OrderResponse
    ) : PaymentState
}