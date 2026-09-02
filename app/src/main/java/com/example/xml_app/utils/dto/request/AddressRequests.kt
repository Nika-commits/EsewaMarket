package com.example.xml_app.utils.dto.request

data class CreateAddressRequest(
    val fullName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val label: AddressLabel,
    val isDefaultAddress: Boolean,
    val isDefaultShippingAddress: Boolean
)

enum class AddressLabel {
    Home, Office, Other
}