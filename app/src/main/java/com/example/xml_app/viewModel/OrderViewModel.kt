package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xml_app.activities.OrderActivity
import com.example.xml_app.ui.state.OrderUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _filter = MutableStateFlow(OrderActivity.OrderFilterType.ALL)
    val filter = _filter.asStateFlow()
    private val _uiState = MutableStateFlow<OrderUiState>(OrderUiState.Loading)
    val uiState = _uiState.asStateFlow()
    fun changeFilter(newFilter: OrderActivity.OrderFilterType) {
        _filter.value = newFilter
    }

    suspend fun getOrders() {
        
    }
}