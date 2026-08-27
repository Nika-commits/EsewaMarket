package com.example.xml_app.ui.state

import com.example.xml_app.utils.dto.response.OrderResponse

sealed interface OrderUiState {
    data object Error : OrderUiState
    data object Loading : OrderUiState
    data class Success(
        val orders: List<OrderResponse>
    ) : OrderUiState
}