package com.example.xml_app.viewModel

import android.app.Application
import androidx.credentials.Credential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.firebase.AuthRepository
import com.example.xml_app.utils.formstates.LoginFormState
import com.example.xml_app.utils.validation.LoginValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val validate: LoginValidation = LoginValidation()
    private val app = getApplication<CustomApplicationContext>()
    private val auth = app.auth
    private val _formState = MutableStateFlow(LoginFormState())
    val formState = _formState.asStateFlow()
    private var _result = MutableStateFlow<Boolean?>(null)
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

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            _result.value = AuthRepository.login(email, password, auth)
            _formState.value = _formState.value.copy(isLoading = false)
        }
    }

    fun loginWithGoogle(credential: Credential) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            _result.value = AuthRepository.signInWithGoogle(credential, auth)
            _formState.value = _formState.value.copy(isLoading = false)
        }
    }
}