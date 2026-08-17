package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.entities.User
import com.example.xml_app.models.Product
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class CheckoutViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    private val _cartIds = MutableStateFlow<List<Int>>(emptyList())
    private val productsInCart = MutableStateFlow<List<Product>>(emptyList())
    private val cartCount = MutableStateFlow<Map<Int, Int>>(emptyMap())
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(database.userDao())
    private val productRepository = ProductRepository()

    private val _address = MutableStateFlow("Pulchowk, Lalitpur-20")
    val address = _address.asStateFlow()

    private fun initializeUser() {
        viewModelScope.launch {
            val firebase = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebase.uid)
            _user.value = localUser
        }
    }

    private fun getCartIds() {
        val userId = _user.value?.uid ?: return
        viewModelScope.launch {
            val cart = cartRepository.getOrCreateCart(userId)
            _cartIds.value = cartRepository.getProductIdsInCart(cart.uid)
        }
    }

    private fun getCartProducts() {
        if (_cartIds.value.isEmpty()) return
        viewModelScope.launch {
            try {
                val products = _cartIds.value.map { productId ->
                    val response = productRepository.getProduct(productId)
                    if (!response.isSuccessful) {
                        throw HttpException(response)
                    }
                    response.body()
                        ?: throw Exception("Product $productId body is null")
                }
                productsInCart.value = products
            } catch (e: HttpException) {
                Log.e("Checkout", e.message())
            } catch (e: IOException) {
                Log.e("Checkout", e.message ?: "Network error")
            } catch (e: Exception) {
                Log.e("Checkout", e.message ?: "Exception occurred")
            }
        }
    }
}