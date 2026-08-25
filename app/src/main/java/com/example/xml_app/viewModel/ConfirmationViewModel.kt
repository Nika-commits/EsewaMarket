package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.response.OrderResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ConfirmationViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val orderRepository = OrderRepository()
    private val userRespository = UserRepository(app.database.userDao())
    private val _orderResponse = MutableStateFlow<OrderResponse?>(null)
    val orderResponse = _orderResponse.asStateFlow()
    private var _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    fun getOrder(orderId: Int) {
        viewModelScope.launch {
            try {
                val firebaseUser = app.auth.currentUser ?: return@launch
                val token = firebaseUser.getIdToken(false).await().token ?: return@launch
                val orderResponse = orderRepository.getOrderById(
                    token = token,
                    id = orderId
                )
                if (!orderResponse.isSuccessful) {
                    Log.e("Confirmation", "${orderResponse.code()}")
                    return@launch
                }
                Log.d("Confirmation", "${orderResponse.body()}")
                _orderResponse.value = orderResponse.body()
            } catch (e: Exception) {
                Log.e("Confirmation", "${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
