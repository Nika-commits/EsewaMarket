package com.example.xml_app.ui.state

import com.example.xml_app.utils.dto.response.OrderResponse

sealed interface ConfirmationUiState {
    data object Loading : ConfirmationUiState

    data class Success(
        val order: OrderResponse
    ) : ConfirmationUiState

    data object Error : ConfirmationUiState
}