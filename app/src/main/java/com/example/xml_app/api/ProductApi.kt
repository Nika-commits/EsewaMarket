package com.example.xml_app.api

import com.example.xml_app.models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductApi {

    @GET("api/product")
    suspend fun getFeaturedProducts(): Response<List<Product>>

    @POST("api/product")
    suspend fun postProduct(@Body product: Product)

    @GET("/api/product/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

}