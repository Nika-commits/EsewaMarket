package com.example.xml_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//const val BASE = "192.168.1.71"
const val BASE = "10.19.16.196"

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://${BASE}:5077/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val productApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val addressApi: MapboxApi by lazy {
        retrofit.create(MapboxApi::class.java)
    }
}
