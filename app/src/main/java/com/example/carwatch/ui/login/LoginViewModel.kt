package com.example.carwatch.ui.login

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carwatch.domain.repository.AuthRepository
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private fun findActivity(context: Context): ComponentActivity? {
        var currentContext = context
        while (currentContext is ContextWrapper) {
            if (currentContext is ComponentActivity) return currentContext
            currentContext = currentContext.baseContext
        }
        return null
    }

    fun onSignInClick(context: Context) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            
            // Login bypass: Ignora o CredentialManager para evitar erros em outros PCs
            // e autentica diretamente no MockAuthRepository
            val authResult = authRepository.signInWithGoogle(
                idToken = "bypass_token",
                displayName = "Dev User",
                photoUrl = null
            )
            
            if (authResult.isSuccess) {
                _uiState.value = LoginUiState(isSuccess = true)
            } else {
                _uiState.value = LoginUiState(error = "Falha ao entrar no modo dev")
            }
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential
        
        try {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            val displayName = googleIdTokenCredential.displayName
            val photoUrl = googleIdTokenCredential.profilePictureUri?.toString()

            val authResult = authRepository.signInWithGoogle(
                idToken = idToken,
                displayName = displayName,
                photoUrl = photoUrl
            )
            
            if (authResult.isSuccess) {
                _uiState.value = LoginUiState(isSuccess = true)
            } else {
                _uiState.value = LoginUiState(error = "Falha na autenticação")
            }
        } catch (e: Exception) {
            _uiState.value = LoginUiState(error = "Erro ao processar login do Google: ${e.message}")
        }
    }
}
