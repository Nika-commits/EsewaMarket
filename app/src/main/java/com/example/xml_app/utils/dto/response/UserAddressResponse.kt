package com.example.xml_app.utils.dto.response

import com.example.xml_app.utils.dto.request.AddressLabel

data class UserAddressResponse(
    val id: Int,
    val userId: Int,
    val fullName: String,
    val phoneNumber: String,
    val fullAddress: String,
    val label: AddressLabel,
    val isDefaultAddress: Boolean,
    val isDefaultShippingAddress: Boolean,
    val createdAt: String,
    val updatedAt: String
)