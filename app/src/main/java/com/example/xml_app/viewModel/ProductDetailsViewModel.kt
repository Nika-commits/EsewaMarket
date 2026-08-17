package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.entities.CartItem
import com.example.xml_app.entities.User
import com.example.xml_app.models.Product
import com.example.xml_app.models.ProductUiModel
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.FavouriteRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _product = MutableStateFlow<Product?>(null)
    private val _selectedColor = MutableStateFlow<String?>(null)
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val repository = ProductRepository()
    val selectedColor = _selectedColor.asStateFlow()
    private val _user = MutableStateFlow<User?>(null)
    private val _cartItem = MutableStateFlow<CartItem?>(null)
    private val _isFavourite = MutableStateFlow(false)
    private val userRepository = UserRepository(database.userDao())
    private val cartRepository = CartRepository(database.cartDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())

    init {
        initializeUser()
    }

    fun isLoggedIn(): Boolean {
        return _user.value != null
    }

    private fun initializeUser() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebaseUser.uid)

            _user.value = localUser
        }

    }

    val product: StateFlow<ProductUiModel?> = combine(
        _product,
        _cartItem,
        _isFavourite
    ) { product, cartItem, isFavourite ->
        product?.let {
            ProductUiModel(
                product = it,
                isFavourite = isFavourite,
                cartCount = cartItem?.quantity ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )

    init {
        initializeUser()
    }


    fun selectColor(color: String) {
        _selectedColor.value = color
    }

    private fun observeProductState() {
        val user = _user.value ?: return
        val product = _product.value ?: return

        viewModelScope.launch {
            val cart = cartRepository.getOrCreateCart(user.uid)
            launch {
                cartRepository.observeCartItem(cart.uid, product.id)
                    .collect { _cartItem.value = it }
            }

            launch {
                favouriteRepository.observeFavouriteIds(user.uid)
                    .collect { _isFavourite.value = product.id in it }
            }

        }

    }

    fun toggleFavourite() {
        val user = _user.value ?: return
        val product = _product.value ?: return
        viewModelScope.launch {
            favouriteRepository.toggleFavourite(user.uid, product.id)
        }
    }

    fun cartIncrement() {
        val user = _user.value ?: return
        val product = _product.value ?: return
        viewModelScope.launch {
            cartRepository.increment(user.uid, product.id)
        }
    }

    fun decrementCart() {
        val user = _user.value ?: return
        val product = _product.value ?: return
        viewModelScope.launch {
            cartRepository.decrement(user.uid, product.id)
        }
    }


    fun getProduct(id: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getProduct(id)
                if (!response.isSuccessful) {
                    Log.e("Product", "Unsuccessful Response")
                }

                val fetchedProduct = response.body() ?: return@launch
                _product.value = fetchedProduct
                observeProductState()
            } catch (e: Exception) {
                Log.e("Product", "${e.message}")
            }
        }
    }
}
