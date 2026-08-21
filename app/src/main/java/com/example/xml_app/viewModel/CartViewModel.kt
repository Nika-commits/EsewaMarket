package com.example.xml_app.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val _productsInCart = MutableStateFlow<List<Product>>(emptyList())
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    private val _cartId = MutableStateFlow<Int?>(null)
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val productRepository = ProductRepository()
    private val cartRepository = CartRepository(database.cartDao())
    private val userRepository = UserRepository(app.database.userDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())

    private val _isCartLoading = MutableStateFlow(false)
    val isCartLoading = _isCartLoading.asStateFlow()
    val productsInCart: StateFlow<List<ProductUiModel>> = combine(
        _productsInCart,
        _cartItems,
        _favouriteIds
    ) { products, cartItems, favouriteIds ->
        val cartItemByProduct = cartItems.associateBy { it.productId }

        products.map { product ->
            ProductUiModel(
                product = product,
                isFavourite = product.id in favouriteIds,
                cartCount = cartItemByProduct[product.id]?.quantity ?: 0
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    val totalPrice: StateFlow<Int> = productsInCart.map { products ->
        products.sumOf { item ->
            item.product.price * item.cartCount
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = 0
    )

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun initializeUser() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebaseUser.uid) ?: return@launch
            _user.value = localUser

            initializeAndObserveCart(localUser.uid)
            observeFavourites(localUser.uid)
        }
    }


    private suspend fun initializeAndObserveCart(userId: Int) {
        val cart = cartRepository.getOrCreateCart(userId)
        _cartId.value = cart.uid
        database.cartDao().observeCartItems(cart.uid)
            .collect { cartItems ->
                _cartItems.value = cartItems
                getProductsInCart()
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

    fun cartIncrement(productId: Int) {
        val user = _user.value ?: return
        viewModelScope.launch {
            cartRepository.increment(user.uid, productId)
        }
    }

    fun cartDecrement(productId: Int) {
        val user = _user.value ?: return
        viewModelScope.launch {
            cartRepository.decrement(user.uid, productId)
        }
    }

    fun getProductsInCart() {
        val idsInCart = _cartItems.value.map { it.productId }
        if (idsInCart.isEmpty()) {
            _productsInCart.value = emptyList()
            return
        }
        viewModelScope.launch {
            _isCartLoading.value = true
            _error.value = null
            try {
                val products: List<Product> = idsInCart.map { productId ->
                    async {
                        val response = productRepository.getProduct(productId)

                        if (!response.isSuccessful) {
                            throw Exception("${response.code()}")
                        }
                        response.body() ?: throw Exception("Empty Response")
                    }
                }.awaitAll()
                _productsInCart.value = products
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load products in cart"
            } finally {
                _isCartLoading.value = false
            }
        }
    }

    val recommendedProducts: Flow<PagingData<ProductUiModel>> = combine(
        productRepository.getRecommendedProduct().cachedIn(viewModelScope),
        _cartItems,
        _favouriteIds
    ) { pagingData, cartItems, favouriteIds ->
        pagingData.map { product ->
            val cartItemsByProduct = cartItems.associateBy { it.productId }
            ProductUiModel(
                product = product,
                cartCount = cartItemsByProduct[product.id]?.quantity ?: 0,
                isFavourite = product.id in favouriteIds
            )
        }
    }


}