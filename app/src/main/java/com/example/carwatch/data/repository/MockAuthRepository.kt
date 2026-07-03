package com.example.carwatch.data.repository

import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor() : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun signInWithGoogle(
        idToken: String,
        displayName: String?,
        photoUrl: String?
    ): Result<Unit> {
        // Mock sign in - using data from Google Credential
        _currentUser.value = User(
            id = "google_$idToken".take(20),
            email = "user@example.com",
            displayName = displayName ?: "Usuário Google",
            photoUrl = photoUrl
        )
        return Result.success(Unit)
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
