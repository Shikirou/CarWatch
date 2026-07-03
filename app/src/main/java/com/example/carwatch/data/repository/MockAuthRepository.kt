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
        // Mock sign in sempre retornando sucesso imediatamente sem depender de token real
        _currentUser.value = User(
            id = "user_id_default",
            email = "dev@carwatch.com",
            displayName = displayName ?: "Desenvolvedor CarWatch",
            photoUrl = photoUrl
        )
        return Result.success(Unit)
    }

    // Método para login rápido para facilitar o trabalho de outros devs
    suspend fun quickSignIn() {
        signInWithGoogle("mock_token", "Dev Guest", null)
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
