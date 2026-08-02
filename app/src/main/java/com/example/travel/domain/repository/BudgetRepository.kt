package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.BudgetPrediction
import com.example.travel.domain.model.TripBudget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllTripBudgets(): Flow<List<TripBudget>>
    suspend fun createTripBudget(budget: TripBudget): Resource<Boolean>
    suspend fun deleteTripBudget(id: String): Resource<Boolean>
    suspend fun predictBudgetExceedance(
        totalBudget: Double,
        totalSpent: Double,
        daysElapsed: Int,
        totalTripDays: Int
    ): BudgetPrediction
}
