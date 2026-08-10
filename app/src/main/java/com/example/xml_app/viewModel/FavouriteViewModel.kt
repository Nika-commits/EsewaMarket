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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class FavouriteViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val app = getApplication<CustomApplicationContext>()
    private val database = app.database
    private val _user = MutableStateFlow<User?>(null)
    private var _favouriteProducts = MutableStateFlow<List<Product>>(emptyList())
    private val _favouriteIds = MutableStateFlow<List<Int>>(emptyList())
    private val productRepository = ProductRepository()
    private val userRepository = UserRepository(database.userDao())
    private val favouriteRepository = FavouriteRepository(database.favouriteDao())


    init {
        getFavouriteProducts()
        initializeUser()
    }

    private fun getFavouriteProducts() {
        if (_favouriteIds.value.isEmpty()) {
            return
        }
        viewModelScope.launch {
            try {
                _favouriteProducts.value = _favouriteIds.value.map {
                    async {
                        val response = productRepository.getProduct(it)

                        if (!response.isSuccessful) {
                            throw HttpException(response)
                        }
                        response.body() ?: throw Exception("Empty Response")

                    }.await()
                }
            } catch (e: Exception) {
                Log.e("Favourite", "${e.message}")
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
                .collect { _favouriteIds.value = it }
        }
    }
}