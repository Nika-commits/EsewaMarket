package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.xml_app.entities.CartItem
import com.example.xml_app.entities.User
import com.example.xml_app.models.Product
import com.example.xml_app.models.ProductUiModel
import com.example.xml_app.repository.CartRepository
import com.example.xml_app.repository.FavouriteRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _hotDealsProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _popularChips = MutableStateFlow<List<String>>(emptyList())
    val popularChips = _popularChips.asStateFlow()
    private val productRepository = ProductRepository()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    private val _cartId = MutableStateFlow<Int?>(null)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(userDao = database.userDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())

    fun isLoggedIn(): Boolean {
        return _user.value != null
    }

    val featuredProducts: StateFlow<List<ProductUiModel>> = combine(
        _featuredProducts,
        _cartItems,
        _favouriteIds
    ) { products, cartItems, favouriteIds ->

        val cartItemsByProduct = cartItems.associateBy { it.productId }

        products.map { product ->
            ProductUiModel(
                product = product,
                isFavourite = product.id in favouriteIds,
                cartCount = cartItemsByProduct[product.id]?.quantity ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val hotDealsProducts: StateFlow<List<ProductUiModel>> = combine(
        _hotDealsProducts,
        _cartItems,
        _favouriteIds
    ) { products, cartItems, favouriteIds ->

        val cartItemsByProduct = cartItems.associateBy { it.productId }
        products.map { product ->
            ProductUiModel(
                product = product,
                isFavourite = product.id in favouriteIds,
                cartCount = cartItemsByProduct[product.id]?.quantity ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val recommendedProducts: Flow<PagingData<ProductUiModel>> = combine(
        productRepository.getRecommendedProduct().cachedIn(viewModelScope),
        _cartItems,
        _favouriteIds
    ) { pagingData, cartItems, favouriteIds ->
        val cartItemsByProduct = cartItems.associateBy { it.productId }

        pagingData.map { product ->
            ProductUiModel(
                product = product,
                isFavourite = product.id in favouriteIds,
                cartCount = cartItemsByProduct[product.id]?.quantity ?: 0
            )
        }
    }

    fun initializeUser() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebaseUser.uid) ?: return@launch
            _user.value = localUser

            initializeCart(localUser.uid)
            observeFavourites(localUser.uid)
        }
    }

    private suspend fun initializeCart(userId: Int) {
        val cart = cartRepository.getOrCreateCart(userId)
        _cartId.value = cart.uid
        observeCartItems(cart.uid)
    }

    private fun observeCartItems(cartId: Int) {
        viewModelScope.launch {
            database.cartDao()
                .observeCartItems(cartId)
                .collect {
                    _cartItems.value = it
                }
        }
    }

    private fun observeFavourites(userId: Int) {
        viewModelScope.launch {
            favouriteRepository
                .observeFavouriteIds(userId)
                .collect {
                    _favouriteIds.value = it.toSet()
                }
        }
    }

    fun toggleFavourite(productId: Int) {
        val user = _user.value ?: return

        viewModelScope.launch {
            favouriteRepository.toggleFavourite(user.uid, productId)
        }
    }

    fun cartIncrement(productId: Int) {
        val user = _user.value ?: return

        viewModelScope.launch {
            cartRepository.increment(user.uid, productId)
        }
    }

    fun decrementCart(productId: Int) {
        val user = _user.value ?: return

        viewModelScope.launch {
            cartRepository.decrement(user.uid, productId)
        }
    }

    fun getFeaturedProduct() {
        viewModelScope.launch {
            try {
                val response = productRepository.getFeaturedProducts()
                if (response.isSuccessful) {
                    _featuredProducts.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.d("API", e.message.toString())
            }
        }
    }

    fun getHotDealsProducts() {
        viewModelScope.launch {
            try {
                val response = productRepository.getHotDealsProduct()
                if (response.isSuccessful) {
                    _hotDealsProducts.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.d("API", e.message.toString())
            }
        }
    }

    fun getPopularChips() {
        viewModelScope.launch {
            try {
                val response = productRepository.getPopularChips()
                if (response.isSuccessful) {
                    _popularChips.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("API", "${e.message}")
            }
        }
    }
}