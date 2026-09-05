package com.example.xml_app.api

import com.example.xml_app.models.Product
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {
    @GET("api/product")
    suspend fun getProducts(
        @Query("category") category: String?,
        @Query("search") search: String?,
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): Response<List<Product>>

    @POST("api/product")
    suspend fun postProduct(@Body product: Product)

    @GET("/api/product/{id}")
    suspend fun getProduct(@Path("id") id: Int): Response<Product>

    @GET("/api/product/popular")
    suspend fun getPopularChips(): Response<List<String>>

    @POST("/api/product/check-promocode")
    suspend fun checkPromoCode(
        @Body promoCode: String
    ): Response<Unit>

    @GET("/api/product/search-suggestions")
    suspend fun getSearchSuggestions(
        @Query("query") query: String
    ): Response<List<String>>
}
