package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.models.Product
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.OrderRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CheckoutAuthState
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.request.CreateOrderItemRequest
import com.example.xml_app.utils.dto.request.CreateOrderRequest
import com.example.xml_app.utils.dto.request.PaymentOptions
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
    private val orderRepository = OrderRepository()
    private val _address = MutableStateFlow<String?>(null)
    val address = _address.asStateFlow()

    private val _phoneNumber = MutableStateFlow<String?>(null)
    val phoneNumber = _phoneNumber.asStateFlow()
    private val _promoCodeResult = MutableStateFlow<Boolean?>(null)
    val promoCodeResult = _promoCodeResult.asStateFlow()
    private val _promoCode = MutableStateFlow<String?>(null)
    val promoCode = _promoCode.asStateFlow()

    //    private val _paymentOption = MutableStateFlow<String?>(null)
//    val paymentOption = _paymentOption.asStateFlow()
    private val _isCheckingPromoCode = MutableStateFlow(false)
    val isCheckingPromoCode = _isCheckingPromoCode.asStateFlow()
    private val _isOrdering = MutableStateFlow(false)
    val isOrdering = _isOrdering.asStateFlow()

    private val _isUpdatingUser = MutableStateFlow(false)
    val isUpdatingUser = _isUpdatingUser.asStateFlow()
    private val _phoneNumberResult = MutableStateFlow<Boolean?>(null)
    val phoneNumberResult = _phoneNumberResult.asStateFlow()

    fun onPromoCodeChange(newPromoCode: String) {
        _promoCode.value = newPromoCode
    }

    fun onPhoneNumberChange(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun initializeUser() {
        Log.d("Checkout", "Initializeing User")
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
                Log.d("Checkout", "$serverUser")
                if (serverUser == null) {
                    _authState.value = CheckoutAuthState.Unauthorized
                    return@launch
                }
                Log.e("Checkout", "$serverUser")
                _authState.value = CheckoutAuthState.Authorized(serverUser)
                _phoneNumber.value = serverUser.phoneNumber
                _address.value = serverUser.address
                loadCart(serverUser.id)
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
            val promocode = _promoCode.value ?: return@launch
            _isCheckingPromoCode.value = true
            try {
                val response = productRepository.checkPromoCode(promocode)
                _promoCodeResult.value = response.isSuccessful
            } catch (e: Exception) {
                Log.e("Checkout", e.message ?: "Error Occurred Checking PromoCode")
            } finally {
                _isCheckingPromoCode.value = false
            }
        }
    }

    fun updatePhoneNumber() {
        viewModelScope.launch {
            _isUpdatingUser.value = true
            try {
                val updateUserRequest = CreateUserRequest(
                    phone = _phoneNumber.value,
                    username = null,
                    address = null,
                    fullName = null,
                    profilePicture = null
                )

                val firebaseUser = app.auth.currentUser ?: throw Exception("User session timed out")
                val token = firebaseUser.getIdToken(false).await().token
                    ?: throw Exception("Failed to get token")
                val response = userRepository.updateUserProfile(
                    token = token,
                    request = updateUserRequest
                )

                if (!response.isSuccessful) {
                    _phoneNumberResult.value = false
                    return@launch
                }
                val updatedUser = userRepository.getCurrentUser(token)
                if (updatedUser == null) {
                    _phoneNumberResult.value = false
                    return@launch
                }
//                initializeUser()
                _authState.value = CheckoutAuthState.Authorized(updatedUser)
                _phoneNumber.value = updatedUser.phoneNumber
                _phoneNumberResult.value = true
            } catch (e: Exception) {
                _phoneNumberResult.value = false
            } finally {
                _isUpdatingUser.value = false
            }
        }
    }

    suspend fun placeOrder(
        paymentOptions: PaymentOptions
    ): Int? {
        _isOrdering.value = true
        return try {
            val firebaseUser = app.auth.currentUser
            if (firebaseUser == null) {
                Log.e("Checkout", "User is not authenticated")
                return null
            }

            val idToken = firebaseUser.getIdToken(false).await().token
            if (idToken == null) {
                Log.e("Checkout", "Unable to get Firebase Token")
                return null
            }

            val orderItems: List<CreateOrderItemRequest> =
                _productsInCart.value.map { product ->
                    CreateOrderItemRequest(
                        productId = product.id,
                        quantity = _productQuantityMap.value[product.id] ?: 1
                    )
                }

            if (orderItems.isEmpty()) {
                Log.e("Checkout", "Empty Cart")
                return null
            }

            val address = _address.value ?: return null

            val request = CreateOrderRequest(
                address = address,
                phone = "1234567890",
                paymentOption = paymentOptions,
                promocode = if (_promoCode.value != null) _promoCode.value else null,
                items = orderItems
            )
            Log.d("Checkout", request.toString())
            val response = orderRepository.postOrder(
                token = idToken,
                request = request
            )

            if (!response.isSuccessful) {
                Log.e("Checkout", "${response.code()}")
                return null
            }
            if (response.body() == null) return null

            val id = response.body()?.id
            return id
        } catch (e: Exception) {
            Log.e("Checkout", "${e.message}")
        } finally {
            _isOrdering.value = false
        }
    }

}
