package com.example.xml_app.ui.state

import com.example.xml_app.utils.dto.response.UserAddressResponse

sealed interface AddShippingAddressUiState {
    object Error : AddShippingAddressUiState
    object Loading : AddShippingAddressUiState
    class Success(
        val address: UserAddressResponse
    ) : AddShippingAddressUiState
}