package com.example.xml_app.utils

import com.example.xml_app.utils.dto.UserResponse

sealed interface CheckoutAuthState {
    data object Loading : CheckoutAuthState
    data class Authorized(val user: UserResponse) : CheckoutAuthState
    data object Unauthorized : CheckoutAuthState
    data object Error : CheckoutAuthState
}