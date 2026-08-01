package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_cache")
data class RouteCacheEntity(
    @PrimaryKey val id: String,
    val originLat: Double,
    val originLon: Double,
    val destLat: Double,
    val destLon: Double,
    val travelMode: String,
    val distanceMeters: Double,
    val durationSeconds: Double,
    val polylinePointsJson: String,
    val stepsJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
