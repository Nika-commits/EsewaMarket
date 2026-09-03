package com.example.xml_app.utils.dto.request

data class CreateAddressRequest(
    val fullName: String = "",
    val phoneNumber: String = "",
    val fullAddress: String = "",
    val label: AddressLabel = AddressLabel.Home,
    val isDefaultAddress: Boolean = false,
    val isDefaultShippingAddress: Boolean = false
)

enum class AddressLabel {
    Home, Office, Other
}