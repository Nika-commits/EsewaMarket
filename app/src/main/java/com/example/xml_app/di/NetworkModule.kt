package com.example.xml_app.di

import com.example.xml_app.api.MapboxApi
import com.example.xml_app.api.OrderApi
import com.example.xml_app.api.ProductApi
import com.example.xml_app.api.UserApi
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//const val BASE = "192.168.1.71"
const val BASE = "10.19.16.196"
//const val BASE = "10.10.29.125"


val networkModule = module {
    single {
        Retrofit.Builder()
            .baseUrl("http://${BASE}:5077/")
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
    }

    single<ProductApi> {
        get<Retrofit>().create(ProductApi::class.java)
    }

    single<UserApi> {
        get<Retrofit>().create(UserApi::class.java)
    }

    single<OrderApi> {
        get<Retrofit>().create(OrderApi::class.java)
    }

    single<MapboxApi> {
        get<Retrofit>().create(MapboxApi::class.java)
    }
}