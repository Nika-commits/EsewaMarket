package com.example.xml_app.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.Api.RetrofitInstance
import com.example.xml_app.Models.Product
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val featuredProducts = MutableLiveData<List<Product>>()

    fun getFeaturedProduct() {
        viewModelScope.launch {
            val response = RetrofitInstance.api.getFeaturedProducts()
            if (response.isSuccessful) {
                featuredProducts.value = response.body()
            }
        }
    }
}