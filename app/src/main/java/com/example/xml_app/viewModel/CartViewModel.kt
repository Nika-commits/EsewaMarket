package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.xml_app.models.Product
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val productRepository = ProductRepository()
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val _productsInCart = MutableLiveData<List<Product?>>(emptyList())
    val productsInCart: LiveData<List<Product?>> = _productsInCart
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getProductsInCart(idsInCart: List<Int>) {
        if (idsInCart.isEmpty()) {
            _productsInCart.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val products: List<Product?> = idsInCart.map { productId ->
                    async {
                        val response = productRepository.getProduct(productId)

                        if (!response.isSuccessful) {
                            throw Exception("${response.code()}")
                        }
                        response.body()
                    }
                }.awaitAll()

                _productsInCart.value = products
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products in cart"
            } finally {
                _isLoading.value = false
            }
        }
    }
}