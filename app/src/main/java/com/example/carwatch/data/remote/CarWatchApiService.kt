package com.example.carwatch.data.remote

import com.example.carwatch.domain.model.Agency
import com.example.carwatch.domain.model.Vehicle
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CarWatchApiService {
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
