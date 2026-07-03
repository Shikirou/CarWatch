package com.example.carwatch.domain.repository

import kotlinx.coroutines.flow.StateFlow

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?
)

interface AuthRepository {
    val currentUser: StateFlow<User?>
    suspend fun login(email: String, password: String): Result<Unit>
    suspend fun register(name: String, email: String, password: String): Result<Unit>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}
