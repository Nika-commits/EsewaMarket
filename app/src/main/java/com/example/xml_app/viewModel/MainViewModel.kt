package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.FavouriteRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import com.example.xml_app.utils.dto.UserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<UserResponse?>(null)
    val user = _user.asStateFlow()
    private val _cartId = MutableStateFlow<Int?>(null)
    private var _cartCount = MutableStateFlow<Int>(0)
    val cartCount = _cartCount.asStateFlow()
    private val _favouritesCount = MutableStateFlow<Int>(0)
    val favouriteCount = _favouritesCount.asStateFlow()
    private val userRepository = UserRepository(database.userDao())
    private val cartRepository = CartRepository(database.cartDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())


    init {
        initializeUser()
    }

    private fun initializeUser() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebaseUser.uid) ?: return@launch
            val token = firebaseUser.getIdToken(false).await().token ?: return@launch
            val userInfo = userRepository.getCurrentUser(token)
            _user.value = userInfo

            initializeCart(localUser.uid)
            observeFavouriteCount(localUser.uid)
        }
    }

    private suspend fun initializeCart(userId: Int) {
        val cart = cartRepository.getOrCreateCart(userId)
        _cartId.value = cart.uid

        observeCartCount(cart.uid)
    }

    private fun observeCartCount(cartId: Int) {
        viewModelScope.launch {
            cartRepository.observeCartCount(cartId)
                .collect { _cartCount.value = it }
        }
    }

    private fun observeFavouriteCount(userId: Int) {
        viewModelScope.launch {
            favouriteRepository.observeFavouriteCount(userId)
                .collect { _favouritesCount.value = it }
        }
    }


}