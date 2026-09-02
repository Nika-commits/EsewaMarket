package com.example.xml_app.utils.dto

data class CreateUserRequest(
    val username: String?,
    val fullName: String?,
    val profilePicture: String?,
)

data class UserResponse(
    val id: Int,
    val firebaseUid: String,
    val username: String,
    val fullName: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val profilePicture: String?,
)