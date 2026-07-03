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
            val activity = findActivity(context)
            if (activity == null) {
                _uiState.value = LoginUiState(error = "Não foi possível encontrar uma Activity")
                return@launch
            }

            _uiState.value = LoginUiState(isLoading = true)
            
            val credentialManager = CredentialManager.create(activity)
            
            // IMPORTANTE: Substitua pelo seu Web Client ID do Google Cloud Console
            val serverClientId = "103434843639-i5b5flnb89su7l43c9qp0qp0bj2usi2k.apps.googleusercontent.com"
            
            // Usando GetGoogleIdOption com configurações que forçam a exibição do seletor
            val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Mostra todas as contas, não apenas as já logadas no app
                .setServerClientId(serverClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            try {
                val result = credentialManager.getCredential(
                    context = activity,
                    request = request
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Log detalhado para depuração no Logcat
                android.util.Log.e("LoginViewModel", "Erro CredentialManager: ${e.type} - ${e.message}")
                
                val errorMessage = when (e.type) {
                    "android.credentials.GetCredentialException.TYPE_USER_CANCELED" -> 
                        "Login cancelado pelo usuário"
                    "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL" -> 
                        "Conta não encontrada. Verifique: 1. Se há uma conta Google logada no celular. 2. Se o SHA-1 no Console é o de DEBUG (./gradlew signingReport). 3. Se o Client ID no código é o tipo 'WEB'."
                    else -> "Erro (${e.type}): ${e.message}"
                }
                _uiState.value = LoginUiState(error = errorMessage)
            }
catch (e: Exception) {
                _uiState.value = LoginUiState(error = "Erro ao iniciar: ${e.localizedMessage}")
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
