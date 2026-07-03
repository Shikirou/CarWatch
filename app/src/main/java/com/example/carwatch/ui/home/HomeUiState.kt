package com.example.carwatch.ui.home

import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.domain.repository.User

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val agencies: List<Agency> = emptyList(),
        val featuredOffers: List<Vehicle> = emptyList(),
        val searchResults: List<Vehicle> = emptyList(),
        val selectedRadius: Int = 10,
        val currentUser: User? = null
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
