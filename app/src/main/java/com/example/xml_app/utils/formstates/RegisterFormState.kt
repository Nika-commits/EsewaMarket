package com.example.xml_app.utils.formstates

data class RegisterFormState(
    val username: String = "",
    val usernameError: String? = null,

    val email: String = "",
    val emailError: String? = null,

    val password: String = "",
    val passwordError: String? = null,

    val isLoading: Boolean = false
)