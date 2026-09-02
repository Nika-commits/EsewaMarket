package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.ShippingAddressUiState
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShippingAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _state = MutableStateFlow<ShippingAddressUiState>(ShippingAddressUiState.Loading)
    val state = _state.asStateFlow()

    fun getAddresses() {
        viewModelScope.launch {
            try {
                val token = userRepository.getFirebaseToken(app.auth)
                if (token == null) {
                    _state.value = ShippingAddressUiState.Error
                    return@launch
                }

                val addresses = userRepository.getUserAddresses(token)
                _state.value = ShippingAddressUiState.Success(addresses)
            } catch (e: Exception) {
                Log.e("Shipping", "Exception while initializing User: ${e.message}")
                _state.value = ShippingAddressUiState.Error
            }
        }
    }
}
