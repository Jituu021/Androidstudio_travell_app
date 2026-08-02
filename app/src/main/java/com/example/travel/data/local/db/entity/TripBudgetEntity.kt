package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_budgets")
data class TripBudgetEntity(
    @PrimaryKey val id: String,
    val tripName: String,
    val totalBudget: Double,
    val currencySymbol: String = "₹",
    val totalDays: Int = 5,
    val timestamp: Long = System.currentTimeMillis()
)
