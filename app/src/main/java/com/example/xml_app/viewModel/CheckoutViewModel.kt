package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.models.Product
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CheckoutAuthState
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CheckoutViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _authState = MutableStateFlow<CheckoutAuthState>(CheckoutAuthState.Loading)
    val authState = _authState.asStateFlow()
    private val _cartIds = MutableStateFlow<List<Int>>(emptyList())
    private val _productQuantityMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val productQuantityMap = _productQuantityMap.asStateFlow()
    private val _productsInCart = MutableStateFlow<List<Product>>(emptyList())
    val products = _productsInCart.asStateFlow()
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(database.userDao())
    private val productRepository = ProductRepository()
    private val _address = MutableStateFlow("Pulchowk, Lalitpur-20")
    val address = _address.asStateFlow()
    private val _promoCodeResult = MutableStateFlow<Boolean?>(null)
    val promoCodeResult = _promoCodeResult.asStateFlow()
    private val _promoCode = MutableStateFlow("")
    val promoCode = _promoCode.asStateFlow()
    private val _isCheckingPromoCode = MutableStateFlow(false)
    val isCheckingPromoCode = _isCheckingPromoCode.asStateFlow()
    fun onPromoCodeChange(newPromoCode: String) {
        _promoCode.value = newPromoCode
    }

    fun initializeUser() {
        viewModelScope.launch {
            _authState.value = CheckoutAuthState.Loading
            try {
                val firebase = app.auth.currentUser
                if (firebase == null) {
                    _authState.value = CheckoutAuthState.Unauthorized
                    return@launch
                }

                val idToken = firebase.getIdToken(false).await().token
                if (idToken == null) {
                    _authState.value = CheckoutAuthState.Unauthorized
                    return@launch
                }

                val serverUser = userRepository.getCurrentUser(idToken)
                if (serverUser == null) {
                    _authState.value = CheckoutAuthState.Unauthorized
                    return@launch
                }
                loadCart(serverUser.id)
                _authState.value = CheckoutAuthState.Authorized(serverUser)
            } catch (e: Exception) {
                Log.e("Checkout", e.message ?: "Error Occurred during Getting User")
                _authState.value = CheckoutAuthState.Unauthorized
            }
        }
    }

    private suspend fun loadCart(userId: Int) {
        val cart = cartRepository.getOrCreateCart(userId)
        val cartIds = cartRepository.getProductIdsInCart(cart.uid)
        _cartIds.value = cartIds

        _productQuantityMap.value = cartRepository.getCartProductWithQuantity(cart.uid)

        if (cartIds.isEmpty()) {
            _productsInCart.value = emptyList()
            return
        }

        val products = cartIds.map { productId ->
            val response = productRepository.getProduct(productId)
            if (!response.isSuccessful) {
                throw Exception()
            }
            response.body() ?: throw IllegalStateException("Body is empty")
        }
        _productsInCart.value = products
    }

    fun checkPromoCodeValidity() {
        viewModelScope.launch {
            _isCheckingPromoCode.value = true
            try {
                val response = productRepository.checkPromoCode(_promoCode.value)
                _promoCodeResult.value = response.isSuccessful
            } catch (e: Exception) {
                Log.e("Checkout", e.message ?: "Error Occurred Checking PromoCode")
            } finally {
                _isCheckingPromoCode.value = false
            }
        }
    }

}