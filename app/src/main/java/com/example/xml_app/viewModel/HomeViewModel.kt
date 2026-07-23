package com.example.xml_app.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.models.Product
import com.example.xml_app.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _featuredProducts = MutableLiveData<List<Product>>()
    private val _hotDealsProducts = MutableLiveData<List<Product>>()
    val featuredProducts: LiveData<List<Product>> = _featuredProducts
    val hotDealsProducts: LiveData<List<Product>> = _hotDealsProducts

    fun getFeaturedProduct() {
        viewModelScope.launch {
            try {
                val response = repository.getFeaturedProducts()
                if (response.isSuccessful) {
                    _featuredProducts.value = response.body()
                }
            } catch (e: Exception) {
                Log.d("API", e.message.toString())
            }
        }
    }

    fun getHotDealsProducts() {
        viewModelScope.launch {
            try {
                val response = repository.getHotDealsProduct()
                if (response.isSuccessful) {
                    _hotDealsProducts.value = response.body()
                }
            } catch (e: Exception) {
                Log.d("API", e.message.toString())
            }
        }
    }
}