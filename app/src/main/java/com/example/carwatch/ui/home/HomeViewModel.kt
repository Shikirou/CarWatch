package com.example.carwatch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.carwatch.data.location.LocationProvider
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.CarWatchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CarWatchRepository,
    private val authRepository: AuthRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentRadius = 10
    private var lastLocation: Pair<Double, Double>? = null

    init {
        fetchLocationAndLoadData()
    }

    private fun fetchLocationAndLoadData() {
        viewModelScope.launch {
            locationProvider.getCurrentLocation().collect { location ->
                if (location != null) {
                    lastLocation = Pair(location.latitude, location.longitude)
                }
                loadHomeData()
            }
        }
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            
            val lat = lastLocation?.first ?: -23.5505
            val lon = lastLocation?.second ?: -46.6333
            
            combine(
                repository.getNearbyAgencies(lat, lon, currentRadius),
                repository.getFeaturedOffers(),
                authRepository.currentUser
            ) { agencies, featured, user ->
                HomeUiState.Success(
                    agencies = agencies,
                    featuredOffers = featured,
                    selectedRadius = currentRadius,
                    currentUser = user
                )
            }.catch { e ->
                _uiState.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun toggleFavorite(vehicleId: String) {
        viewModelScope.launch {
            repository.toggleFavorite(vehicleId)
        }
    }

    fun onRadiusChanged(newRadius: Int) {
        currentRadius = newRadius
        loadHomeData()
    }

    fun onSearchQueryChanged(query: String) {
        if (query.isBlank()) {
            loadHomeData()
            return
        }
        
        viewModelScope.launch {
            repository.searchVehicles(query).collect { results ->
                val currentState = _uiState.value
                if (currentState is HomeUiState.Success) {
                    _uiState.value = currentState.copy(searchResults = results)
                }
            }
        }
    }
}
