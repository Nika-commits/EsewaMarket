package com.example.xml_app.ui.state

sealed interface AddShippingAddressUiState {
    object Error : AddShippingAddressUiState
    object Loading : AddShippingAddressUiState
    object Success : AddShippingAddressUiState
}

sealed interface AddShippingAddressEvent {
    object IsDeleting : AddShippingAddressEvent

    data class Success(
        val message: String
    ) : AddShippingAddressEvent

    data class Error(
        val message: String
    ) : AddShippingAddressEvent
}
