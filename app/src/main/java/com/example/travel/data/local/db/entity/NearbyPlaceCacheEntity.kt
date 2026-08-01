package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nearby_places_cache")
data class NearbyPlaceCacheEntity(
    @PrimaryKey val id: String,
    val category: String,
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float = 4.5f,
    val distanceMeters: Double = 0.0,
    val status: String = "Open 24/7",
    val phone: String = "+91 1800 22 4433",
    val timestamp: Long = System.currentTimeMillis()
)
