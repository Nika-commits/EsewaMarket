package com.example.xml_app.repository

import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.data.dao.UserDao
import com.example.xml_app.entities.User
import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse
import com.example.xml_app.utils.dto.request.CreateAddressRequest

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
                email = user.email
            )
        )
    }

    suspend fun getLocalUser(firebaseUid: String): User? {
        return userDao.getFirebaseUserById(firebaseUid)
    }

    suspend fun updateUserProfile(
        request: CreateUserRequest,
        token: String
    ) = RetrofitInstance.userApi.updateUserProfile(
        authorization = "Bearer $token",
        request = request
    )

    suspend fun createUserAddress(
        token: String,
        request: CreateAddressRequest
    ) = RetrofitInstance.userApi.createUserAddress(
        authorization = "Bearer $token",
        request = request
    )

    suspend fun getUserAddresses(
        token: String
    ) = RetrofitInstance.userApi.getAddresses(
        authorization = "Bearer $token"
    )

    suspend fun deleteAddress(
        token: String,
        id: Int
    ) = RetrofitInstance.userApi.deleteAddress(
        authorization = "Bearer $token",
        id = id
    )

    suspend fun updateAddress(
        token: String,
        id: Int,
        request: CreateAddressRequest
    ) = RetrofitInstance.userApi.updateAddress(
        authorization = "Bearer $token",
        id = id,
        request = request
    )

    suspend fun setDefaultAddress(
        token: String,
        id: Int,
    ) = RetrofitInstance.userApi.setDefaultAddress(
        authorization = "Bearer $token",
        id = id
    )
}