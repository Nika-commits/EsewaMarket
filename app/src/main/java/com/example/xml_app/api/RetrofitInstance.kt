package com.example.xml_app.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: ProductApi by lazy {
        Retrofit.Builder()
            .baseUrl("http:10.19.16.196:5077")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApi::class.java)
    }

}
