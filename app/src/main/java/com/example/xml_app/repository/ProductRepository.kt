package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.api.getProducts("featured")

    suspend fun getHotDealsProduct() = RetrofitInstance.api.getProducts(null)
    suspend fun getProduct(id: Int) = RetrofitInstance.api.getProduct(id)

}