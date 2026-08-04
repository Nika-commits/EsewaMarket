package com.example.xml_app.viewModel

import android.app.Application
import androidx.credentials.Credential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.firebase.AuthRepository
import com.example.xml_app.utils.formstates.RegisterFormState
import com.example.xml_app.utils.validation.RegisterValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private val TAG = "Register"

class RegisterViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val validate: RegisterValidation = RegisterValidation()
    private val app = getApplication<CustomApplicationContext>()
    private val auth = app.auth
    private val _formState = MutableStateFlow(RegisterFormState())
    val formState = _formState.asStateFlow()
    private val _result = MutableStateFlow<Boolean?>(null)
    val result = _result.asStateFlow()
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

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            val firebaseUser = AuthRepository.register(email, password, auth)

            _result.value = firebaseUser != null

            _formState.value = _formState.value.copy(isLoading = false)
        }
    }

    fun registerWithGoogle(credential: Credential) {
        viewModelScope.launch {
            val firebaseUser = AuthRepository.signInWithGoogle(credential, auth)

            _result.value = firebaseUser != null
        }
    }
}
