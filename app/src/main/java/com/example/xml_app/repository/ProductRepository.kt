package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.productApi.getProducts("featured")
    suspend fun getHotDealsProduct() = RetrofitInstance.productApi.getProducts(null)
    suspend fun getProduct(id: Int) = RetrofitInstance.productApi.getProduct(id)

}