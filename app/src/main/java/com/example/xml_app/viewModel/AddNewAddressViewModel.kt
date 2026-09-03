package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AddNewAddressViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _formData = MutableStateFlow(CreateAddressRequest())
    val formData = _formData.asStateFlow()

    fun getCurrentAddress(addressId: Int) {

    }
}
