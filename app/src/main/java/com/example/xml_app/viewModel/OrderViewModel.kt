package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.activities.OrderActivity
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.OrderUiState
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class OrderViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val orderRepository = OrderRepository()
    private val _filter = MutableStateFlow(OrderActivity.OrderFilterType.ALL)
    val filter = _filter.asStateFlow()
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState = _uiState.asStateFlow()
    fun changeFilter(newFilter: OrderActivity.OrderFilterType) {
        _filter.value = newFilter
    }

    fun getOrders() {
        viewModelScope.launch {
            _uiState.value = OrderUiState.Loading
            try {
                val firebaseUser = app.auth.currentUser
                if (firebaseUser == null) {
                    _uiState.value = OrderUiState.Error
                    return@launch
                }
                val token = firebaseUser.getIdToken(false).await().token
                if (token == null) {
                    _uiState.value = OrderUiState.Error
                    return@launch
                }
                val currentUser = userRepository.getCurrentUser(token)
                if (currentUser == null) {
                    _uiState.value = OrderUiState.Error
                    return@launch
                }
                Log.d("Orders", filter.value.label)
                val response = orderRepository.getOrders(
                    token = token,
                    status = _filter.value.label
                )

                if (!response.isSuccessful) {
                    _uiState.value = OrderUiState.Error
                    return@launch
                }

                val orders = response.body()
                if (orders == null) {
                    _uiState.value = OrderUiState.Error
                    return@launch
                }

                _uiState.value = OrderUiState.Success(orders)
            } catch (e: Exception) {
                _uiState.value = OrderUiState.Error
                Log.e("Orders", "exception: ${e.message}")
            }
        }
    }
}