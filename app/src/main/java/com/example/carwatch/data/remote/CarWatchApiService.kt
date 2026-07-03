package com.example.carwatch.data.remote

import com.example.carwatch.data.remote.model.UserLoginRequest
import com.example.carwatch.data.remote.model.UserRegisterRequest
import com.example.carwatch.data.remote.model.UserResponse
import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import retrofit2.http.*

interface CarWatchApiService {
    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterRequest): UserResponse

    @POST("auth/login")
    suspend fun login(@Body request: UserLoginRequest): UserResponse

    @GET("vehicles/featured")
    suspend fun getFeaturedOffers(@Query("userId") userId: String?): List<Vehicle>

    @GET("agencies/nearby")
    suspend fun getNearbyAgencies(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int
    ): List<Agency>

    @GET("agencies/{id}/vehicles")
    suspend fun getAgencyVehicles(@Path("id") agencyId: String): List<Vehicle>

    @GET("vehicles/search")
    suspend fun searchVehicles(@Query("q") query: String, @Query("userId") userId: String?): List<Vehicle>

    @POST("vehicles/favorite/{id}")
    suspend fun toggleFavorite(@Path("id") vehicleId: String, @Query("userId") userId: String)

    @POST("vehicles")
    suspend fun postVehicle(@Body vehicle: Vehicle): Vehicle

    @GET("notifications")
    suspend fun getNotifications(): List<NotificationResponse>

    @GET("chats")
    suspend fun getChats(@Query("userId") userId: String): List<ChatResponse>
}

data class NotificationResponse(val id: String, val title: String, val message: String, val time: String)
data class ChatResponse(
    val id: String, 
    val userName: String, 
    val userPhoto: String?, 
    val vehicleName: String, 
    val vehiclePhoto: String?, 
    val lastMessage: String, 
    val time: String,
    val unreadCount: Int
)
