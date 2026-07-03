package com.example.carwatch.data.remote.model

data class UserRegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: Int,
    val name: String,
    val email: String,
    val message: String
)
