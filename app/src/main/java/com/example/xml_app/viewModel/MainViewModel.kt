package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.xml_app.entities.User
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    private val _cartId = MutableStateFlow<Int?>(null)

    private var _cartCount = MutableStateFlow<Int>(0)
    val cartCount = _cartCount.asStateFlow()

    private val userRepository = UserRepository(database.userDao())
    private val cartRepository = CartRepository(database.userDao(), database.cartDao())

    init {

    }

    private fun getCartCount() {
        cartCount = cartRepository.getOrCreateCart()
    }


}