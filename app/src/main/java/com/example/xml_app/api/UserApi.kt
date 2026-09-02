package com.example.xml_app.api

import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import com.example.xml_app.utils.dto.response.UserAddressResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @POST("api/user")
    suspend fun createUser(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserRequest
    ): Response<UserResponse>

    @GET("/api/user")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<UserResponse>

    @POST("/api/user/update-profile")
    suspend fun updateUserProfile(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserRequest
    ): Response<UserResponse>

    @POST("/api/user/address")
    suspend fun createUserAddress(
        @Header("Authorization") authorization: String,
        @Body request: CreateAddressRequest
    ): Response<UserAddressResponse>

    @GET("/api/user/address")
    suspend fun getAddresses(
        @Header("Authorization") authorization: String,
    ): Response<List<UserAddressResponse>>

    @DELETE("/api/user/address/{id}")
    suspend fun deleteAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    )

    @PUT("/api/user/address/{id}")
    suspend fun updateAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int,
        @Body request: CreateAddressRequest
    )

    @PATCH("/api/user/address/{id}/set-default")
    suspend fun setDefaultAddress(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    )
}