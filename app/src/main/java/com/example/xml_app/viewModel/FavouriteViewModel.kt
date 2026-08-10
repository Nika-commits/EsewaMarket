package com.example.xml_app.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.xml_app.entities.User
import com.example.xml_app.models.Product
import com.example.xml_app.repository.FavouriteRepository
import com.example.xml_app.repository.ProductRepository
import com.example.xml_app.repository.UserRepository
import com.example.xml_app.utils.CustomApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

private const val T = "Favourite"

class FavouriteViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    private var _favouriteProducts = MutableStateFlow<List<Product>>(emptyList())
    val favouriteProducts = _favouriteProducts.asStateFlow()
    private val _favouriteIds = MutableStateFlow<List<Int>>(emptyList())
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository(database.userDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())


    init {
        initializeUser()
        getFavouriteProducts()
    }

    private fun getFavouriteProducts() {
        Log.d(T, "products Fetching")
        if (_favouriteIds.value.isEmpty()) {
            Log.d(T, "${_favouriteIds.value.size}")
            return
        }
        viewModelScope.launch {
            try {
                _favouriteProducts.value = _favouriteIds.value.map {
                    async {
                        val response = productRepository.getProduct(it)

                        if (!response.isSuccessful) {
                            Log.e(T, "$response")
                            throw HttpException(response)
                        }
                        response.body() ?: throw Exception("Empty Response")

                    }
                }.awaitAll()

                Log.d(T, "${_favouriteProducts.value}")
            } catch (e: Exception) {
                Log.e(T, "${e.message}")
            }
        }
    }

    private fun initializeUser() {
        viewModelScope.launch {
            val firebaseUser = app.auth.currentUser ?: return@launch
            val localUser = userRepository.getLocalUser(firebaseUser.uid) ?: return@launch

            _user.value = localUser
            observeFavourites(localUser.uid)

        }
    }

    private fun observeFavourites(userId: Int) {
        viewModelScope.launch {
            favouriteRepository.observeFavouriteIds(userId)
                .collect {
                    _favouriteIds.value = it
                    getFavouriteProducts()
                }
        }
    }
}