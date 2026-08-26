package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xml_app.activities.OrderActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class OrderViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _filter = MutableStateFlow(OrderActivity.OrderFilterType.ALL)
    val filter = _filter.asStateFlow()

    fun changeFilter(newFilter: OrderActivity.OrderFilterType) {
        _filter.value = newFilter
    }

}