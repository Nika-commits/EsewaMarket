package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.entities.User
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    private val _cartId = MutableStateFlow<Int?>(null)
    private val _cartIds = MutableStateFlow<List<Int>>(emptyList())
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(database.userDao())

    private fun initializeUser() {
        viewModelScope.launch {
            val firebase = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebase.uid)
            _user.value = localUser
        }
    }

    private fun getCartItems() {
        val userId = _user.value?.uid ?: return
        viewModelScope.launch {
            val cart = cartRepository.getOrCreateCart(userId)

            val cartItems = cartRepository
        }
    }

    private fun getProductsInCart() {

    }
}