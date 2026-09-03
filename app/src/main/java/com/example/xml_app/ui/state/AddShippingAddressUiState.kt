package com.example.xml_app.ui.state

sealed interface AddShippingAddressUiState {
    object Error : AddShippingAddressUiState
    object Loading : AddShippingAddressUiState
    object Success : AddShippingAddressUiState
}