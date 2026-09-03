package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.AddShippingAddressUiState
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import com.example.xml_app.utils.dto.response.UserAddressResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddNewAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _state = MutableStateFlow<AddShippingAddressUiState>(AddShippingAddressUiState.Loading)
    val state = _state.asStateFlow()
    private val _formData = MutableStateFlow(CreateAddressRequest())
    val formData = _formData.asStateFlow()

    fun getCurrentAddress(addressId: Int): UserAddressResponse? {
        viewModelScope.launch {

        }
    }
}
