package com.example.travel.domain.usecase.travel

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Expense
import com.example.travel.domain.repository.TravelRepository
import javax.inject.Inject

class AddExpenseUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    suspend operator fun invoke(expense: Expense): Resource<Long> {
        if (expense.amount <= 0.0) return Resource.Error("Expense amount must be greater than zero")
        return travelRepository.addExpense(expense)
    }
}
