package com.example.xml_app.api

import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

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
}