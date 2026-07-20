package com.example.xml_app.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.models.Product
import com.example.xml_app.repository.ProductRepository
import kotlinx.coroutines.launch

class ProductDetailsViewModel : ViewModel() {
    private val repository = ProductRepository()
    private val _product = MutableLiveData<Product>()

    val product: LiveData<Product> = _product

    fun getProduct(id: Int) {
        viewModelScope.launch {
            val response = repository.getProduct(id)

            if (response.isSuccessful) {
                _product.value = response.body()
            }
        }
    }
}