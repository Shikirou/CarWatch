package com.example.carwatch.domain.model

data class Agency(
    val id: String,
    val name: String,
    val cnpj: String,
    val logoUrl: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val reviewCount: Int
)
