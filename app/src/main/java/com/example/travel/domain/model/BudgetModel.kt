package com.example.travel.domain.model

data class TripBudget(
    val id: String = "",
    val tripName: String,
    val totalBudget: Double,
    val currencySymbol: String = "₹",
    val totalDays: Int = 5,
    val timestamp: Long = System.currentTimeMillis()
)

data class BudgetPrediction(
    val isExceedingBudget: Boolean,
    val projectedTotalSpent: Double,
    val projectedOverspendAmount: Double,
    val dailyBurnRate: Double,
    val recommendedDailyLimit: Double,
    val savingRecommendation: String
)
