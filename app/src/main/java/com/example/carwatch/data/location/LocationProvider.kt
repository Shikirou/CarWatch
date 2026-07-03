package com.example.carwatch.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationProvider @Inject constructor(
    private val client: FusedLocationProviderClient,
    @ApplicationContext private val context: Context
) {
    @SuppressLint("MissingPermission")
    fun getCurrentLocation(): Flow<Location?> = callbackFlow {
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                trySend(location)
                close() // Close the flow after receiving one result
            }
            .addOnFailureListener {
                trySend(null)
                close() // Close even on failure to avoid hanging
            }
        awaitClose { /* No-op cleanup */ }
    }
}
