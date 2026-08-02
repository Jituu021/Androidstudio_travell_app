package com.example.travel.domain.model

data class TimelineEvent(
    val id: String = "",
    val tripId: String,
    val eventType: String, // "JOURNAL", "PHOTO", "VOICE", "EXPENSE", "RESTAURANT"
    val title: String,
    val description: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class MemoryRoute(
    val id: String = "",
    val tripId: String,
    val polylineJson: String = "[]",
    val totalDistanceKm: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)

data class TripStatistics(
    val tripId: String,
    val totalDistanceKm: Double = 145.8,
    val placesVisitedCount: Int = 18,
    val citiesVisitedCount: Int = 3,
    val photosCount: Int = 42,
    val entriesCount: Int = 12,
    val totalExpensesAmount: Double = 8450.0,
    val restaurantsExploredCount: Int = 9,
    val lastUpdated: Long = System.currentTimeMillis()
)
