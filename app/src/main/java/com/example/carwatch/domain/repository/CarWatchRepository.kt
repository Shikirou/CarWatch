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
    suspend fun postVehicle(vehicle: Vehicle): Result<Unit>
    fun getNotifications(): Flow<List<Notification>>
    fun getChats(): Flow<List<ChatEntry>>
}

data class Notification(val id: String, val title: String, val message: String, val time: String)
data class ChatEntry(
    val id: String,
    val userName: String,
    val userPhoto: String?,
    val vehicleName: String,
    val vehiclePhoto: String?,
    val lastMessage: String,
    val time: String,
    val unreadCount: Int
)
