package com.example.xml_app.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.paging.RecommendedProductsPagingSource

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.productApi.getProducts("featured", 0, 4)
    suspend fun getHotDealsProduct() = RetrofitInstance.productApi.getProducts(null, 10, 4)
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
}