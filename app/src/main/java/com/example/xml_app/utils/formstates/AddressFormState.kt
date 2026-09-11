package com.example.xml_app.utils.formstates

import com.example.xml_app.utils.dto.request.AddressLabel

data class AddressFormState(
    val fullName: String = "",
    val fullNameError: String? = null,

    val mobileNumber: String = "",
    val mobileNumberError: String? = null,

    val address: String = "",
    val addressError: String? = null,

    val label: AddressLabel = AddressLabel.Home,
    val labelError: String? = null,

    val isDefaultAddress: Boolean = false,
    val isDefaultShippingAddress: Boolean = false
)