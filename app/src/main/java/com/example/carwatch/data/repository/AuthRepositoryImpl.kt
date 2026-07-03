package com.example.carwatch.data.repository

import android.content.Context
import com.example.carwatch.data.remote.CarWatchApiService
import com.example.carwatch.data.remote.model.UserLoginRequest
import com.example.carwatch.data.remote.model.UserRegisterRequest
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: CarWatchApiService,
    @ApplicationContext private val context: Context
) : AuthRepository {
    private val prefs = context.getSharedPreferences("carwatch_prefs", Context.MODE_PRIVATE)
    private val _currentUser = MutableStateFlow<User?>(loadUserFromPrefs())
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private fun loadUserFromPrefs(): User? {
        val id = prefs.getString("user_id", null) ?: return null
        val email = prefs.getString("user_email", "") ?: ""
        val name = prefs.getString("user_name", "") ?: ""
        return User(id, email, name, null)
    }

    private fun saveUserToPrefs(user: User) {
        prefs.edit().apply {
            putString("user_id", user.id)
            putString("user_email", user.email)
            putString("user_name", user.displayName)
            apply()
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.login(UserLoginRequest(email, password))
            val user = User(
                id = response.id.toString(),
                email = response.email,
                displayName = response.name,
                photoUrl = null
            )
            _currentUser.value = user
            saveUserToPrefs(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(name: String, email: String, password: String): Result<Unit> {
        return try {
            val response = apiService.register(UserRegisterRequest(name, email, password))
            val user = User(
                id = response.id.toString(),
                email = response.email,
                displayName = response.name,
                photoUrl = null
            )
            _currentUser.value = user
            saveUserToPrefs(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        prefs.edit().clear().apply()
        _currentUser.value = null
    }

    override fun isUserLoggedIn(): Boolean {
        return _currentUser.value != null
    }
}
