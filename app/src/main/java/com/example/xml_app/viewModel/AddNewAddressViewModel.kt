package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.AddShippingAddressUiState
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddNewAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _state = MutableStateFlow<AddShippingAddressUiState>(AddShippingAddressUiState.Success)
    val state = _state.asStateFlow()
    private val _formData = MutableStateFlow(CreateAddressRequest())
    val formData = _formData.asStateFlow()

    fun getCurrentAddress(addressId: Int) {
        viewModelScope.launch {
            try {
                val token = userRepository.getFirebaseToken(app.auth)
                if (token == null) {
                    _state.value = AddShippingAddressUiState.Error
                    return@launch
                }
                val address = userRepository.getAddressById(token, addressId)
                if (address == null) {
                    _state.value = AddShippingAddressUiState.Error
                    return@launch
                }
                _formData.value = CreateAddressRequest(
                    fullName = address.fullName,
                    fullAddress = address.fullAddress,
                    phoneNumber = address.phoneNumber,
                    label = address.label,
                    isDefaultAddress = address.isDefaultAddress,
                    isDefaultShippingAddress = address.isDefaultShippingAddress,
                )

            } catch (e: Exception) {
                Log.e("Address", "getCurrentAddress: ${e.message}")
                _state.value = AddShippingAddressUiState.Error
            }
        }
    }
}
