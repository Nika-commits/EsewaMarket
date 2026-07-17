package com.example.xml_app.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.Models.Product
import com.example.xml_app.Repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()
    private val _featuredProducts = MutableLiveData<List<Product>>()

    val featuredProducts: LiveData<List<Product>> = _featuredProducts

    fun getFeaturedProduct() {
        viewModelScope.launch {
            val response = repository.getFeaturedProducts()
            if (response.isSuccessful) {
                _featuredProducts.value = response.body()
            }
        }
    }
}