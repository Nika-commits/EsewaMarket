package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.credentials.Credential
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.firebase.AuthRepository
import com.example.xml_app.utils.formstates.RegisterFormState
import com.example.xml_app.utils.validation.RegisterValidation
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private val TAG = "Register"

class RegisterViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val validate: RegisterValidation = RegisterValidation()
    private val app = getApplication<CustomApplicationContext>()
    private val db = FirebaseFirestore.getInstance()
    private val auth = app.auth
    private val repository = UserRepository(app.database.userDao())
    private val _formState = MutableStateFlow(RegisterFormState())
    val formState = _formState.asStateFlow()
    private val _result = MutableStateFlow<Boolean?>(null)
    val result = _result.asStateFlow()
    fun register(
        fullName: String,
        username: String,
        email: String,
        password: String,
    ) {
        val fullNameResult = validate.validateFullName(fullName)
        val usernameResult = validate.validateUsername(username)
        val emailResult = validate.validateEmail(email)
        val passwordResult = validate.validatePassword(password)

        _formState.value = RegisterFormState(
            fullName = fullName,
            username = username,
            email = email,
            password = password,
            fullNameError = fullNameResult.errorMessage,
            usernameError = usernameResult.errorMessage,
            emailError = emailResult.errorMessage,
            passwordError = passwordResult.errorMessage
        )

        if (!usernameResult.successful || !emailResult.successful || !passwordResult.successful || !fullNameResult.successful) return

        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)

            try {
                val firebaseUser = AuthRepository.register(email, password, auth)
                if (firebaseUser == null) {
                    _result.value = false
                    return@launch
                }

                val token = firebaseUser.getIdToken(false).await().token
                if (token == null) {
                    _result.value = false
                    return@launch
                }

                val request = CreateUserRequest(
                    username = username,
                    fullName = "",
                    address = null,
                    phone = null
                )
                val FireStoreUser = hashMapOf(
                    "username" to username,
                    "full_name" to ""
                )
                repository.createUser(token, request)
                _result.value = true
                return@launch
            } catch (e: Exception) {
                Log.d(TAG, "${e.message}")
                _result.value = false
            } finally {
                _formState.value = _formState.value.copy(isLoading = false)
            }

        }
    }

    fun registerWithGoogle(credential: Credential) {
        viewModelScope.launch {
            _formState.value = _formState.value.copy(isLoading = true)

            try {
                val firebaseUser = AuthRepository.signInWithGoogle(credential, auth)
                if (firebaseUser == null) {
                    _result.value = false
                    return@launch
                }

                val token = firebaseUser.getIdToken(false).await().token
                if (token == null) {
                    _result.value = false
                    return@launch
                }

                val request = CreateUserRequest(
                    username = firebaseUser.displayName ?: firebaseUser.email?.substringBefore("@")
                    ?: "user",
                    fullName = firebaseUser.displayName ?: "",
                    address = null,
                    phone = firebaseUser.phoneNumber
                )

                repository.createUser(token, request)
                _result.value = true
            } catch (e: Exception) {
                _result.value = false
                Log.e("Register", "${e.message}")
            } finally {
                _formState.value = _formState.value.copy(isLoading = false)
            }
        }
    }
}
