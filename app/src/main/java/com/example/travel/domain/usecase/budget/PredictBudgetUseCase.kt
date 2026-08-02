package com.example.travel.domain.usecase.budget

import com.example.travel.domain.model.BudgetPrediction
import com.example.travel.domain.repository.BudgetRepository
import javax.inject.Inject

class PredictBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(
        totalBudget: Double,
        totalSpent: Double,
        daysElapsed: Int,
        totalTripDays: Int
    ): BudgetPrediction {
        return budgetRepository.predictBudgetExceedance(totalBudget, totalSpent, daysElapsed, totalTripDays)
    }
}
