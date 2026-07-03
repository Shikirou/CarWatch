package com.example.carwatch.domain.model

data class VendorInfo(
    val id: String,
    val name: String,
    val photoUrl: String?,
    val phoneNumber: String,
    val rating: Float,
    val reviewCount: Int
)
