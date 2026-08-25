package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.ui.state.ConfirmationUiState
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ConfirmationViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val orderRepository = OrderRepository()
    private val _uiState = MutableStateFlow<ConfirmationUiState>(ConfirmationUiState.Loading)

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
}
