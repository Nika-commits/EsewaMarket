package com.example.xml_app.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.models.Product
import com.example.xml_app.utils.paging.RecommendedProductsPagingSource

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.productApi.getProducts("featured", null, 0, 4)
    suspend fun getHotDealsProduct() = RetrofitInstance.productApi.getProducts(null, null, 10, 4)
    suspend fun getSearchProducts(
        category: String? = null,
        search: String,
        page: Int = 0
    ): List<Product>? {
        return try {
            val response = RetrofitInstance.productApi.getProducts(
                category,
                search,
                page,
                10
            )
            if (!response.isSuccessful) {
                Log.e("Search", "Unsuccessful response: ${response.code()}")
            }
            response.body()
        } catch (e: Exception) {
            Log.e("Search", "Exception in getSearchProducts: ${e.message}")
            emptyList()
        }
    }

    fun getRecommendedProduct() = Pager(
        config = PagingConfig(
            pageSize = 4,
            initialLoadSize = 4,
            prefetchDistance = 1,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = {
            RecommendedProductsPagingSource(
                RetrofitInstance.productApi
            )
        }
    ).flow

    suspend fun getProduct(id: Int) = RetrofitInstance.productApi.getProduct(id)
    suspend fun getPopularChips() = RetrofitInstance.productApi.getPopularChips()
    suspend fun checkPromoCode(promoCode: String) = RetrofitInstance.productApi.checkPromoCode(promoCode)
    suspend fun getSearchSuggestions(query: String): List<String> {
        val response = RetrofitInstance.productApi.getSearchSuggestions(query)
        if (!response.isSuccessful) {
            throw Exception("Could not get Search Suggestions")
        }
        return response.body() ?: emptyList()
    }
}