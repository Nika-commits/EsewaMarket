package com.example.xml_app.viewModel

import androidx.lifecycle.ViewModel
import com.example.xml_app.utils.validation.LoginValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel(
    private val validate: LoginValidation = LoginValidation()
) : ViewModel() {
    private var _formState = MutableStateFlow(LoginValidation())
    private val formState = _formState.asStateFlow()

    fun login() {


    }
}