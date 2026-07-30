package com.example.xml_app.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.xml_app.utils.formstates.RegisterFormState
import com.example.xml_app.utils.validation.RegisterValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RegisterViewModel(
    private val validate: RegisterValidation = RegisterValidation()
) : ViewModel() {
    private val _formState = MutableStateFlow(RegisterFormState())
    val formState = _formState.asStateFlow()

    fun register(
        username: String,
        email: String,
        password: String
    ) {
        val usernameResult = validate.validateUsername(username)
        val emailResult = validate.validateEmail(email)
        val passwordResult = validate.validatePassword(password)

        _formState.value = RegisterFormState(
            username = username,
            email = email,
            password = password,
            usernameError = usernameResult.errorMessage,
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage
        )

        if (!usernameResult.successful || !emailResult.successful || !passwordResult.successful) return

        Log.d("Register", "$username , $email , $password")


    }
}