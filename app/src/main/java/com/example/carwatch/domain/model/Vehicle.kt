package com.example.carwatch.domain.model

data class Vehicle(
    val id: String,
    val agencyId: String,
    val brand: String,
    val model: String,
    val version: String,
    val year: Int,
    val mileage: Int,
    val price: Double,
    val originalPrice: Double? = null,
    val transmission: String,
    val fuel: String,
    val imageUrlsList: List<String>,
    val description: String,
    val location: String,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val isFavorite: Boolean = false,
    val statusTag: String? = null, // "NEW", "DROP", "SELL"
    val vendorInfo: VendorInfo? = null,
    val specs: Map<String, String> = emptyMap(), // "Acceleration" to "4.4s", "Max Power" to "520 Hm"
    val color: String = "Prata",
    val ipvaPaid: Boolean = true
)
