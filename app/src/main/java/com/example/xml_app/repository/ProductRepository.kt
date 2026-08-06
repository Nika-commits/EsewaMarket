package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.productApi.getProducts("featured", 0, 4)
    suspend fun getHotDealsProduct() = RetrofitInstance.productApi.getProducts(null, 1, 4)
    suspend fun getRecommendedProduct() = RetrofitInstance.productApi.getProducts(null, 2, 4)
    suspend fun getProduct(id: Int) = RetrofitInstance.productApi.getProduct(id)
    suspend fun getPopularChips() = RetrofitInstance.productApi.getPopularChips()
}