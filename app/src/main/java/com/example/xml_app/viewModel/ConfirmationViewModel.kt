package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConfirmationViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val orderRepository = OrderRepository()
    private val userRespository = UserRepository(app.database.userDao())
    private var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()
    fun getOrder(orderId: Int) {
        viewModelScope.launch {

        }
    }
}
