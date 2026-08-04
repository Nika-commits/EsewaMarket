package com.example.xml_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("http://10.19.196:5077")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ProductApi: ProductApi by lazy {
        retrofit.create(ProductApi::class.java)
    }

    val UserApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}
