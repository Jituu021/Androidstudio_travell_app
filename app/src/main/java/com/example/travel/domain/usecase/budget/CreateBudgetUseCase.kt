package com.example.travel.domain.usecase.budget

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.TripBudget
import com.example.travel.domain.repository.BudgetRepository
import javax.inject.Inject

class CreateBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(budget: TripBudget): Resource<Boolean> {
        return budgetRepository.createTripBudget(budget)
    }
}
