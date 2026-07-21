package com.example.xml_app.repository

import android.content.Context
import androidx.core.content.ContentProviderCompat
import com.example.xml_app.api.RetrofitInstance

class ProductRepository {
    suspend fun getFeaturedProducts() = RetrofitInstance.api.getFeaturedProducts()

    suspend fun getProduct(id: Int) = RetrofitInstance.api.getProduct(id)


}