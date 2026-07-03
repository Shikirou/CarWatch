package com.example.carwatch.data.remote

import com.example.carwatch.data.remote.model.UserLoginRequest
import com.example.carwatch.data.remote.model.UserRegisterRequest
import com.example.carwatch.data.remote.model.UserResponse
import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CarWatchApiService {
    @POST("auth/register")
    suspend fun register(@Body request: UserRegisterRequest): UserResponse

    @POST("auth/login")
    suspend fun login(@Body request: UserLoginRequest): UserResponse

    @GET("vehicles/featured")
    suspend fun getFeaturedOffers(): List<Vehicle>

    @GET("agencies/nearby")
    suspend fun getNearbyAgencies(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int
    ): List<Agency>

    @GET("agencies/{id}/vehicles")
    suspend fun getAgencyVehicles(@Path("id") agencyId: String): List<Vehicle>

    @GET("vehicles/search")
    suspend fun searchVehicles(@Query("q") query: String): List<Vehicle>
}
