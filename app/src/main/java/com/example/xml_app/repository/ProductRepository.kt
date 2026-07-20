package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.api.getFeaturedProducts()

    suspend fun getProduct(id: Int) = RetrofitInstance.api.getProduct(id)
}