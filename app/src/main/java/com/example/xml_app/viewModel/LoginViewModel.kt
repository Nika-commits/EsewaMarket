package com.example.xml_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.xml_app.utils.firebase.AuthRepository
import com.example.xml_app.utils.formstates.LoginFormState
import com.example.xml_app.utils.validation.LoginValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    private val validate: LoginValidation = LoginValidation()
) : ViewModel() {
    private var _formState = MutableStateFlow(LoginFormState())
    val formState = _formState.asStateFlow()
    private var _result = MutableStateFlow(false)
    val result = _result.asStateFlow()

    fun login(email: String, password: String) {
        val emailResult = validate.validateEmail(email)
        val passwordResult = validate.validatePassword(password)

        _formState.value = LoginFormState(
            email = email,
            password = password,
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage
        )

        if (!emailResult.successful || !passwordResult.successful) return

        _result.value = AuthRepository.login(email, password)
    }
}