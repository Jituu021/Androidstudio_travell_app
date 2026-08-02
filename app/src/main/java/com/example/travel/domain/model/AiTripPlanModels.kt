package com.example.travel.domain.model

data class TripActivity(
    val id: String = "",
    val timeSlot: String, // "Morning", "Afternoon", "Evening"
    val title: String,
    val locationName: String,
    val category: String, // "Attraction", "Food", "Rest", "Adventure"
    val durationMinutes: Int = 90,
    val estimatedCost: Double = 0.0,
    val notes: String = ""
)

data class TripDay(
    val dayNumber: Int,
    val dateStr: String,
    val title: String,
    val activities: List<TripActivity>,
    val dailyCostEstimate: Double
)

data class AiTripPlan(
    val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budgetAmount: Double,
    val numberOfTravelers: Int,
    val travelStyle: String,
    val interests: List<String>,
    val days: List<TripDay>,
    val totalEstimatedCost: Double,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)
