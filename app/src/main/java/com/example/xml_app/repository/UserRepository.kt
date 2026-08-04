package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.utils.dto.CreateUserRequest

class UserRepository {

    suspend fun createUser(token: String, request: CreateUserRequest) =
        RetrofitInstance.UserApi.createUser(authorization = "Bearer $token", request = request)

    suspend fun getCurrentUser(token: String) =
        RetrofitInstance.UserApi.getCurrentUser(authorization = "Bearer $token")

}