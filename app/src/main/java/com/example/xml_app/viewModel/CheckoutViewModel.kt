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
import okio.IOException
import retrofit2.HttpException

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
            val firebase = app.auth.currentUser
            if (firebase == null) {
                _authState.value = CheckoutAuthState.Unauthorized
                return@launch
            }
            try {
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
                _authState.value = CheckoutAuthState.Authorized(serverUser)
                getCartIds(serverUser.id)
            } catch (e: Exception) {
                Log.e("Checkout", e.message ?: "Error Occurred during Getting User")
                _authState.value = CheckoutAuthState.Unauthorized
            }
        }
    }

    private suspend fun getCartIds(userId: Int) {
        val cart = cartRepository.getOrCreateCart(userId)
        _cartIds.value = cartRepository.getProductIdsInCart(cart.uid)
        getCartProducts()
        getProductQuantityMap(cart.uid)
    }

    suspend fun getCartProducts() {
        if (_cartIds.value.isEmpty()) return

        try {
            val products = _cartIds.value.map { productId ->
                val response = productRepository.getProduct(productId)
                if (!response.isSuccessful) {
                    Log.d("Checkout", "HTTP Errors")
                    throw HttpException(response)
                }
                response.body()
                    ?: throw Exception("Product $productId body is null")
            }
            _productsInCart.value = products
            Log.d("Checkout", "Products Fetched")
        } catch (e: HttpException) {
            Log.e("Checkout", e.message())
        } catch (e: IOException) {
            Log.e("Checkout", e.message ?: "Network error")
        } catch (e: Exception) {
            Log.e("Checkout", e.message ?: "Exception occurred")
        }
    }

    suspend fun getProductQuantityMap(cartId: Int) {
        _productQuantityMap.value = cartRepository.getCartProductWithQuantity(cartId)
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