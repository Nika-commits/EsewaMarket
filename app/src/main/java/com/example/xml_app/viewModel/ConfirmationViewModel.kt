package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.ui.state.ConfirmationOrderUiState
import com.example.xml_app.ui.state.ConfirmationUiState
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.request.OrderStatus
import com.example.xml_app.utils.dto.request.UpdateOrderStatusRequest
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
    private val userRepository = UserRepository(app.database.userDao())
    private val cartRepository = CartRepository(app.database.cartDao())
    private val _uiState = MutableStateFlow<ConfirmationUiState>(ConfirmationUiState.Loading)
    val uiState = _uiState.asStateFlow()
    private val _confirmationOrderUiState = MutableStateFlow<ConfirmationOrderUiState>(ConfirmationOrderUiState.Idle)
    val confirmationOrderUiState = _confirmationOrderUiState.asStateFlow()
    fun getOrder(orderId: Int) {
        viewModelScope.launch {
            _uiState.value = ConfirmationUiState.Loading
            try {
                val firebaseUser = app.auth.currentUser
                if (firebaseUser == null) {
                    _uiState.value = ConfirmationUiState.Error
                    return@launch
                }

                val token = firebaseUser.getIdToken(false).await().token
                if (token == null) {
                    _uiState.value = ConfirmationUiState.Error
                    return@launch
                }

                val orderResponse = orderRepository.getOrderById(
                    token = token,
                    id = orderId
                )

                if (!orderResponse.isSuccessful) {
                    Log.e("Confirmation", "${orderResponse.code()}")
                    _uiState.value = ConfirmationUiState.Error
                    return@launch
                }
                val order = orderResponse.body()
                Log.d("Confirmation", "$order")
                if (order == null) {
                    _uiState.value = ConfirmationUiState.Error
                    return@launch
                }
                _uiState.value = ConfirmationUiState.Success(order)
            } catch (e: Exception) {
                Log.e("Confirmation", "${e.message}")
                _uiState.value = ConfirmationUiState.Error
            }
        }
    }

    fun updateOrderStatusToPending() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState !is ConfirmationUiState.Success) return@launch
            _confirmationOrderUiState.value = ConfirmationOrderUiState.Loading

            val orderId = currentState.order.id
            try {
                val firebaseUser = app.auth.currentUser
                if (firebaseUser == null) {
                    _confirmationOrderUiState.value = ConfirmationOrderUiState.Error
                    return@launch
                }

                val token = firebaseUser.getIdToken(false).await().token
                if (token == null) {
                    _confirmationOrderUiState.value = ConfirmationOrderUiState.Error
                    return@launch
                }

                val response = orderRepository.updateOrderStatus(
                    id = orderId,
                    token = token,
                    request = UpdateOrderStatusRequest(
                        status = OrderStatus.Pending
                    )
                )

                if (!response.isSuccessful) {
                    Log.e("Confirmation", "Failed to update: ${response.code()}")
                    _confirmationOrderUiState.value = ConfirmationOrderUiState.Error
                    return@launch
                }
                val responseOrder = response.body()
                if (responseOrder == null) {
                    _confirmationOrderUiState.value = ConfirmationOrderUiState.Error
                    return@launch
                }
                try {
                    removeFromCart(responseOrder)
                } catch (e: Exception) {
                    Log.e("Confirmation", "Failed to delete from cart. ${e.message}")
                }
                _confirmationOrderUiState.value = ConfirmationOrderUiState.Success(responseOrder)
            } catch (e: Exception) {
                Log.e("Confirmation", "${e.message}")
                _confirmationOrderUiState.value = ConfirmationOrderUiState.Error
            }
        }
    }

    fun removeFromCart(
        order: OrderResponse
    ) {
        viewModelScope.launch {
            try {
                val firebaseUser = app.auth.currentUser ?: return@launch
                val token = firebaseUser.getIdToken(false).await().token ?: return@launch
                val currentUser = userRepository.getCurrentUser(token) ?: return@launch
                val cart = cartRepository.getOrCreateCart(currentUser.id)
                val productIds = order.orderItems.map { it.productId }.distinct()

                cartRepository.removeCartIds(
                    cartId = cart.uid,
                    productIds = productIds
                )

            } catch (e: Exception) {
                Log.e("Confirmation", "${e.message}")
            }
        }
    }
}
