package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.data.dao.UserDao
import com.example.xml_app.entities.User
import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse

class UserRepository(
    private val userDao: UserDao
) {
    suspend fun createUser(token: String, request: CreateUserRequest): UserResponse {
        val response =
            RetrofitInstance.userApi.createUser(authorization = "Bearer $token", request = request)

        if (!response.isSuccessful) {
            throw Exception("${response.code()}")
        }

        val remoteUser = response.body() ?: throw Exception("Empty Response")
        saveUserLocally(remoteUser)
        return remoteUser
    }

    suspend fun getCurrentUser(token: String): UserResponse? {
        val response = RetrofitInstance.userApi.getCurrentUser(authorization = "Bearer $token")
        return if (!response.isSuccessful) {
            null
        } else {
            response.body()
        }
    }

    private suspend fun saveUserLocally(user: UserResponse) {
        userDao.upsert(
            User(
                uid = user.id,
                firebaseUid = user.firebaseUid,
                fullName = user.fullName,
                username = user.username,
                address = user.address,
                phone = user.phone
            )
        )
    }

    suspend fun getLocalUser(firebaseUid: String): User? {
        return userDao.getFirebaseUserById(firebaseUid)
    }

    suspend fun updateUserProfile(
        request: CreateUserRequest,
        token: String
    ) = RetrofitInstance.userApi.updateUserProfile(authorization = token, request = request)
}