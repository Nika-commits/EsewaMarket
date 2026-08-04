package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.ProductApi.getProducts("featured")
    suspend fun getHotDealsProduct() = RetrofitInstance.ProductApi.getProducts(null)
    suspend fun getProduct(id: Int) = RetrofitInstance.ProductApi.getProduct(id)

}