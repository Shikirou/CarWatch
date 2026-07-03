package com.example.carwatch.data.repository

import com.example.carwatch.data.remote.CarWatchApiService
import com.example.carwatch.data.remote.model.UserLoginRequest
import com.example.carwatch.data.remote.model.UserRegisterRequest
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: CarWatchApiService
) : AuthRepository {
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(UserLoginRequest(email, password))
            _currentUser.value = User(
                id = response.id.toString(),
                email = response.email,
                displayName = response.name,
                photoUrl = null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.register(UserRegisterRequest(name, email, password))
            // Após registro, podemos opcionalmente logar o usuário ou apenas retornar sucesso
            _currentUser.value = User(
                id = response.id.toString(),
                email = response.email,
                displayName = response.name,
                photoUrl = null
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
