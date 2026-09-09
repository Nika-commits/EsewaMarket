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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class SearchViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val userRepository = UserRepository(app.database.userDao())
    private val cartRepository = CartRepository(app.database.cartDao())
    private val favouriteRepository = FavouriteRepository(app.database.favouriteDao())
    private val productRepository = ProductRepository()
    private val _user = MutableStateFlow<User?>(null)
    val user = _user.asStateFlow()
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private val _favouriteIds = MutableStateFlow<Set<Int>>(emptySet())
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()
    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions = _suggestions.asStateFlow()
    private val _products = MutableStateFlow<List<Product>>(emptyList())
    private val _isLoadingProducts = MutableStateFlow(false)
    val isLoadingProducts = _isLoadingProducts.asStateFlow()
    private val _hasMoreProducts = MutableStateFlow(true)
    val hasMoreProducts = _hasMoreProducts.asStateFlow()

    private var currentPage = 0

    companion object {
        private const val PAGE_SIZE = 10
    }

    fun onChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    init {
        observeSearchQuery()
    }

    fun isLoggedIn(): Boolean {
        return _user.value != null
    }

    fun initialize() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val user = userRepository.getLocalUser(firebaseUser.uid) ?: return@launch
            _user.value = user
            val cart = cartRepository.getOrCreateCart(user.uid)

            launch {
                cartRepository.observeCart(cart.uid)
                    .collectLatest { _cartItems.value = it }
            }

            launch {
                favouriteRepository.observeFavouriteIds(user.uid)
                    .collectLatest { _favouriteIds.value = it.toSet() }
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQuery
                .debounce(300.milliseconds)
                .distinctUntilChanged()
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _suggestions.value = emptyList()
                        return@collectLatest
                    }

                    try {
                        val result = productRepository.getSearchSuggestions(query)
                        _suggestions.value = result
                        Log.d("Search", _suggestions.value.toString())
                    } catch (e: Exception) {
                        _suggestions.value = emptyList()
                        Log.e("Search", "failed to observe: ${e.message}")
                    }
                }
        }
    }

    fun getSearchedProducts() {
        currentPage = 0
        _hasMoreProducts.value = true
        _products.value = emptyList()
        loadMoreProducts(currentPage)
//        viewModelScope.launch {
//            val response = productRepository.getSearchProducts(
//                null,
//                _searchQuery.value,
//            )
//            if (response == null) {
//                _products.value = emptyList()
//            } else {
//                _products.value = response
//            }
//        }
    }

    private fun loadMoreProducts(page: Int) {
        if (_isLoadingProducts.value) return
        if (!_hasMoreProducts.value) return

        viewModelScope.launch {
            if (page != 0) {
                _isLoadingProducts.value = true
            }
            try {
                val response = productRepository.getSearchProducts(
                    category = null,
                    search = _searchQuery.value,
                    page = page
                )

                if (response.isNullOrEmpty()) {
                    _hasMoreProducts.value = false
                } else {
                    _products.value += response
                    currentPage = page
                    if (response.size < PAGE_SIZE) {
                        _hasMoreProducts.value = false
                    }
                }
            } finally {
                _isLoadingProducts.value = false
            }
        }
    }

    fun loadNextPage() {
        Log.d("Search", "Loading Next Page: $currentPage")
        if (_isLoadingProducts.value) return
        if (!_hasMoreProducts.value) return

        loadMoreProducts(currentPage + 1)
    }

    val products: StateFlow<List<ProductUiModel>> = combine(
        _products,
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
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toggleFavourite(productId: Int) {
        viewModelScope.launch {
            val user = _user.value ?: return@launch
            favouriteRepository.toggleFavourite(
                user.uid,
                productId
            )
        }
    }

    fun incrementCart(productId: Int) {
        viewModelScope.launch {
            val user = _user.value ?: return@launch
            cartRepository.increment(
                user.uid,
                productId
            )
        }
    }

    fun decrementCart(productId: Int) {
        viewModelScope.launch {
            val user = _user.value ?: return@launch
            cartRepository.decrement(
                user.uid,
                productId
            )
        }
    }

}