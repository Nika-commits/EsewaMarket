package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.firebase.AuthRepository
import com.example.xml_app.utils.formstates.LoginFormState
import com.example.xml_app.utils.validation.LoginValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val validate: LoginValidation = LoginValidation()
    private val app = getApplication<CustomApplicationContext>()
    private val repository = UserRepository(userDao = app.database.userDao())
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

            try {

                val firebaseUser = AuthRepository.login(email, password, auth)

                val token = firebaseUser?.getIdToken(false)?.await()?.token ?: run {
                    _result.value = false
                    return@launch
                }

                repository.getCurrentUser(token)

                _result.value = true
            } catch (e: Exception) {
                _result.value = false
                Log.d("Login", "${e.message}")
            } finally {
                _formState.value = _formState.value.copy(isLoading = false)
            }

        }
    }

    fun loginWithGoogle(credential: Credential) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)
            val firebaseUser = AuthRepository.signInWithGoogle(credential, auth)

            _result.value = firebaseUser != null

            _formState.value = _formState.value.copy(isLoading = false)
        }
    }
}