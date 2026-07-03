package com.example.carwatch.ui.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.CarWatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SellViewModel @Inject constructor(
    private val repository: CarWatchRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _isSuccess = MutableStateFlow(false)
    val isSuccess = _isSuccess.asStateFlow()

    fun publishVehicle(
        marca: String, modelo: String, km: String, 
        preco: String, descricao: String
    ) {
        viewModelScope.launch {
            val user = authRepository.currentUser.value ?: return@launch
            val newVehicle = Vehicle(
                id = UUID.randomUUID().toString(),
                agencyId = "1", // Default for dev
                brand = marca,
                model = modelo,
                year = 2024,
                mileage = km.toIntOrNull() ?: 0,
                price = preco.toDoubleOrNull() ?: 0.0,
                description = descricao,
                location = "Local do Usuário",
                imageUrlsList = listOf("https://placehold.co/600x400?text=$marca+$modelo"),
                transmission = "Automático",
                fuel = "Flex",
                version = "Padrão",
                color = "Preto"
            )
            
            val result = repository.postVehicle(newVehicle)
            if (result.isSuccess) {
                _isSuccess.value = true
            }
        }
    }
}
