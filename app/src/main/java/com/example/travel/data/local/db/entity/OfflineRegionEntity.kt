package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_regions")
data class OfflineRegionEntity(
    @PrimaryKey val id: String,
    val name: String,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
    val minZoom: Int = 10,
    val maxZoom: Int = 15,
    val totalTiles: Int = 0,
    val downloadedTiles: Int = 0,
    val sizeMb: Double = 0.0,
    val isComplete: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
