package com.example.carwatch.data.repository

import com.example.carwatch.data.remote.CarWatchApiService
import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import com.example.carwatch.domain.repository.ChatEntry
import com.example.carwatch.domain.repository.Notification
import com.example.carwatch.domain.repository.AuthRepository
import com.example.carwatch.domain.repository.CarWatchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarWatchRepositoryImpl @Inject constructor(
    private val apiService: CarWatchApiService,
    private val authRepository: AuthRepository
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
            val userId = authRepository.currentUser.value?.id
            emit(apiService.getFeaturedOffers(userId))
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
            val userId = authRepository.currentUser.value?.id
            emit(apiService.searchVehicles(query, userId))
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun toggleFavorite(vehicleId: String) {
        try {
            val userId = authRepository.currentUser.value?.id ?: return
            apiService.toggleFavorite(vehicleId, userId)
        } catch (e: Exception) {
            // Log error or handle
        }
    }

    override suspend fun postVehicle(vehicle: Vehicle): Result<Unit> {
        return try {
            apiService.postVehicle(vehicle)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getNotifications(): Flow<List<Notification>> = flow {
        try {
            val response = apiService.getNotifications()
            emit(response.map { Notification(it.id, it.title, it.message, it.time) })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getChats(): Flow<List<ChatEntry>> = flow {
        try {
            val userId = authRepository.currentUser.value?.id ?: return@flow
            val response = apiService.getChats(userId)
            emit(response.map { 
                ChatEntry(it.id, it.userName, it.userPhoto, it.vehicleName, it.vehiclePhoto, it.lastMessage, it.time, it.unreadCount) 
            })
        } catch (e: Exception) {
            emit(emptyList())
        }
    }
}
