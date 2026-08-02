package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timeline_events")
data class TimelineEventEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val eventType: String, // "JOURNAL", "PHOTO", "VOICE", "EXPENSE", "RESTAURANT"
    val title: String,
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "memory_routes")
data class MemoryRouteEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val polylineJson: String,
    val totalDistanceKm: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trip_statistics")
data class TripStatisticsEntity(
    @PrimaryKey val tripId: String,
    val totalDistanceKm: Double = 0.0,
    val placesVisitedCount: Int = 0,
    val citiesVisitedCount: Int = 0,
    val photosCount: Int = 0,
    val entriesCount: Int = 0,
    val totalExpensesAmount: Double = 0.0,
    val restaurantsExploredCount: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)
