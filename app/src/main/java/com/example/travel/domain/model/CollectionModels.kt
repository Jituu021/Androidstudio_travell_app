package com.example.travel.domain.model

data class MemoryCollection(
    val id: String = "",
    val title: String,
    val category: String = "SMART", // "SMART", "CUSTOM"
    val coverPhotoId: String = "",
    val isArchived: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class SearchIndex(
    val id: String = "",
    val memoryId: String,
    val memoryType: String, // "PHOTO", "JOURNAL", "FOOD", "PLACE"
    val searchableText: String,
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class TripHighlight(
    val id: String = "",
    val tripId: String,
    val highlightTitle: String,
    val topMemories: List<String> = emptyList(),
    val mostVisitedPlace: String = "Scenic Central Park",
    val favoriteRestaurant: String = "The Spice Route Bistro",
    val timestamp: Long = System.currentTimeMillis()
)
