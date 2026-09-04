package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.activities.AddNewAddressActivity
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.AddShippingAddressEvent
import com.example.xml_app.ui.state.AddShippingAddressUiState
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddNewAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _state = MutableStateFlow<AddShippingAddressUiState>(AddShippingAddressUiState.Success)
    val state = _state.asStateFlow()
    private val _event = MutableSharedFlow<AddShippingAddressEvent>()
    val event = _event.asSharedFlow()
    private val _formData = MutableStateFlow(CreateAddressRequest())
    val formData = _formData.asStateFlow()
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()
    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()
    fun getCurrentAddress(addressId: Int) {
        viewModelScope.launch {
            _state.value = AddShippingAddressUiState.Loading
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
                _state.value = AddShippingAddressUiState.Success
            } catch (e: Exception) {
                Log.e("Address", "getCurrentAddress: ${e.message}")
                _state.value = AddShippingAddressUiState.Error
            }
        }
    }

    fun saveAddress(
        mode: AddNewAddressActivity.Companion.MODE,
        id: Int?
    ) {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val token = userRepository.getFirebaseToken(app.auth) ?: return@launch
                when (mode) {
                    AddNewAddressActivity.Companion.MODE.ADD -> {
                        val response = userRepository.createUserAddress(token, _formData.value)
                        if (response != null) {
                            _snackbarMessage.emit("Address Created Successfully")
                        } else {
                            _snackbarMessage.emit("Failed to create Address")
                        }
                    }

                    AddNewAddressActivity.Companion.MODE.EDIT -> {
                        if (id == null) return@launch
                        val response = userRepository.updateAddress(
                            token = token,
                            id = id,
                            request = _formData.value
                        )
                        if (response != null) {
                            _snackbarMessage.emit("Address Edited Successfully")
                        } else {
                            _snackbarMessage.emit("Failed to edit Address")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("Address", "SaveAddress: ${e.message}")
                _snackbarMessage.emit("Failed to save address")
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _event.emit(AddShippingAddressEvent.IsDeleting)
            try {
                val token = userRepository.getFirebaseToken(app.auth)
                if (token == null) {
                    _snackbarMessage.emit("Authentication Error")
                    return@launch
                }
                userRepository.deleteAddress(
                    token = token,
                    id = id
                )
                _snackbarMessage.emit("Address Deleted Successfully")
                _event.emit(AddShippingAddressEvent.Success)
            } catch (e: Exception) {
                Log.e("Address", "Failed to delete address: ${e.message}")
                _snackbarMessage.emit("Failed to delete address.")
            }
        }
    }

    fun onEvent(event: AddNewAddressActivity.AddressFormEvent) {
        _formData.update { current ->
            when (event) {
                is AddNewAddressActivity.AddressFormEvent.FullNameChanged -> current.copy(fullName = event.value)
                is AddNewAddressActivity.AddressFormEvent.FullAddressChanged -> current.copy(fullAddress = event.value)
                is AddNewAddressActivity.AddressFormEvent.PhoneNumberChanged -> current.copy(phoneNumber = event.value)
                is AddNewAddressActivity.AddressFormEvent.LabelChanged -> current.copy(label = event.value)
                is AddNewAddressActivity.AddressFormEvent.DefaultAddressChanged -> current.copy(isDefaultAddress = event.value)
                is AddNewAddressActivity.AddressFormEvent.DefaultShippingAddressChanged -> current.copy(
                    isDefaultShippingAddress = event.value
                )
            }
        }
    }
}
