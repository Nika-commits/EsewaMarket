package com.example.xml_app.Api

import com.example.xml_app.Models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProductApi {

    @GET("/product")
    suspend fun getFeaturedProducts(): Response<List<Product>>

    @POST("/product")
    fun postProduct(@Body product: Product)

}