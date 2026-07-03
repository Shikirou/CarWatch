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
    suspend fun signInWithGoogle(
        idToken: String,
        displayName: String? = null,
        photoUrl: String? = null
    ): Result<Unit>
    suspend fun signOut()
    fun isUserLoggedIn(): Boolean
}
