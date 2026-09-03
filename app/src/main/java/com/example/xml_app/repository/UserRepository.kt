package com.example.xml_app.repository

import android.util.Log
import com.example.xml_app.api.RetrofitInstance
import com.example.xml_app.data.dao.UserDao
import com.example.xml_app.entities.User
import com.example.xml_app.utils.dto.CreateUserRequest
import com.example.xml_app.utils.dto.UserResponse
import com.example.xml_app.utils.dto.request.CreateAddressRequest
import com.example.xml_app.utils.dto.response.UserAddressResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

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

    suspend fun getFirebaseToken(firebase: FirebaseAuth): String? {
        val firebaseUser = firebase.currentUser ?: return null
        val token = firebaseUser.getIdToken(false).await().token ?: return null

        return token
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
    ): UserAddressResponse? {
        val response = RetrofitInstance.userApi.createUserAddress(
            authorization = "Bearer $token",
            request = request
        )
        if (!response.isSuccessful) {
            Log.e("Address", "Failed to create Address: ${response.body()}")
            return null
        }
        if (response.body() == null) {
            return null
        }
        return response.body()
    }

    suspend fun getUserAddresses(
        token: String
    ): List<UserAddressResponse> {
        val response = RetrofitInstance.userApi.getAddresses(
            "Bearer $token"
        )
        if (!response.isSuccessful) {
            throw Exception("Failed to fetch Addresses: ${response.code()}")
        }

        val addresses = response.body() ?: throw Exception("Addresses is null")
        return addresses
    }

    suspend fun getAddressById(
        token: String,
        id: Int
    ): UserAddressResponse? {
        val response = RetrofitInstance.userApi.getAddressById(
            authorization = "Bearer $token",
            id = id
        )

        if (!response.isSuccessful) {
            throw Exception("Failed to fetch address of id: $id , ${response.code()}")
        }

        return response.body()
    }

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
    ): UserAddressResponse? {
        val response = RetrofitInstance.userApi.updateAddress(
            authorization = "Bearer $token",
            id = id,
            request = request
        )

        if (!response.isSuccessful) {
            Log.e("Address", "Failed to Update Address: ${response.code()}")
            return null
        }
        if (response.body() == null) {
            Log.e("Address", "Update Body is null: ${response.body()}")
            return null
        }
        return response.body()
    }

    suspend fun setDefaultAddress(
        token: String,
        id: Int,
    ) = RetrofitInstance.userApi.setDefaultAddress(
        authorization = "Bearer $token",
        id = id
    )
}