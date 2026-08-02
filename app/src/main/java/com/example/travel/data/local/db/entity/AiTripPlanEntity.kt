package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_trip_plans")
data class AiTripPlanEntity(
    @PrimaryKey val id: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val budgetAmount: Double,
    val numberOfTravelers: Int,
    val travelStyle: String,
    val interestsJson: String,
    val itineraryDaysJson: String,
    val totalEstimatedCost: Double,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)
