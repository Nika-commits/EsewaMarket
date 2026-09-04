package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.ShippingAddressUiEvent
import com.example.xml_app.ui.state.ShippingAddressUiState
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShippingAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _state = MutableStateFlow<ShippingAddressUiState>(ShippingAddressUiState.Loading)
    val state = _state.asStateFlow()
    private val _isDeleting = MutableStateFlow(false)
    val isDeleting = _isDeleting.asStateFlow()
    private val _events = MutableSharedFlow<ShippingAddressUiEvent>()
    val events = _events.asSharedFlow()
    private suspend fun fetchAddresses() {
        val token = userRepository.getFirebaseToken(app.auth)
        if (token == null) {
            _state.value = ShippingAddressUiState.Error
            return
        }
        val addresses = userRepository.getUserAddresses(token)
        _state.value = if (addresses.isEmpty()) {
            ShippingAddressUiState.Empty
        } else {
            ShippingAddressUiState.Success(addresses)
        }
    }

    fun getAddresses() {
        viewModelScope.launch {
            _state.value = ShippingAddressUiState.Loading
            try {
                fetchAddresses()
            } catch (e: Exception) {
                Log.e("Shipping", "Exception while initializing User: ${e.message}")
                _state.value = ShippingAddressUiState.Error
            }
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _isDeleting.value = true
            try {
                val token = userRepository.getFirebaseToken(app.auth) ?: return@launch
                userRepository.deleteAddress(
                    token = token,
                    id = id
                )
                fetchAddresses()
                _events.emit(
                    ShippingAddressUiEvent.DeleteSuccess
                )
            } catch (e: Exception) {
                Log.e("Shipping", "Failed to delete address: ${e.message}")
                _events.emit(ShippingAddressUiEvent.Error("Failed to delete address."))
            } finally {
                _isDeleting.value = false
            }
        }
    }
}
