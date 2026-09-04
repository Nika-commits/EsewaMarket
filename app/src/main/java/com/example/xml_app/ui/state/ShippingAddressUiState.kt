package com.example.xml_app.ui.state

import com.example.xml_app.utils.dto.response.UserAddressResponse

sealed interface ShippingAddressUiState {
    object Loading : ShippingAddressUiState
    object Error : ShippingAddressUiState
    class Success(
        val data: List<UserAddressResponse>
    ) : ShippingAddressUiState

    object Empty : ShippingAddressUiState
}

sealed interface ShippingAddressUiEvent {
    data object DeleteSuccess : ShippingAddressUiEvent
    data class Error(
        val message: String
    ) : ShippingAddressUiEvent
}