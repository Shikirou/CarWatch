package com.example.carwatch.domain.repository

import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface CarWatchRepository {
    fun getNearbyAgencies(latitude: Double, longitude: Double, radiusKm: Int): Flow<List<Agency>>
    fun getFeaturedOffers(): Flow<List<Vehicle>>
    fun getAgencyVehicles(agencyId: String): Flow<List<Vehicle>>
    fun searchVehicles(query: String): Flow<List<Vehicle>>
    suspend fun toggleFavorite(vehicleId: String)
}
