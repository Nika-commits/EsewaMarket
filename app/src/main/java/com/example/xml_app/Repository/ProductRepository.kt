package com.example.xml_app.Repository

import com.example.xml_app.Api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.api.getFeaturedProducts()
}