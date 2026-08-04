package com.example.xml_app.api

import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {

    @POST("api/user")
    suspend fun CreateUser(
        @Header("Authorization") authorization: String,
        @Body request: CreateUserRequest
    )

    @GET("/api/user")
    suspend fun GetCurrentUser(
        @Header("Authorization") authorization: String
    ): UserResponse
}