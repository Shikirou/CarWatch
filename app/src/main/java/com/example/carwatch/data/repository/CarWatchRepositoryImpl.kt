package com.example.carwatch.data.repository

import com.example.carwatch.data.remote.CarWatchApiService
import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.domain.repository.CarWatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarWatchRepositoryImpl @Inject constructor(
    private val apiService: CarWatchApiService
) : CarWatchRepository {

    override fun getNearbyAgencies(latitude: Double, longitude: Double, radiusKm: Int): Flow<List<Agency>> = flow {
        try {
            emit(apiService.getNearbyAgencies(latitude, longitude, radiusKm))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getFeaturedOffers(): Flow<List<Vehicle>> = flow {
        try {
            emit(apiService.getFeaturedOffers())
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getAgencyVehicles(agencyId: String): Flow<List<Vehicle>> = flow {
        try {
            emit(apiService.getAgencyVehicles(agencyId))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun searchVehicles(query: String): Flow<List<Vehicle>> = flow {
        try {
            emit(apiService.searchVehicles(query))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun toggleFavorite(vehicleId: String) {
        // Implementar persistência local ou via API se necessário
    }
}
