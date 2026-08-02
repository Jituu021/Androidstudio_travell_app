package com.example.travel.domain.usecase.budget

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Expense
import com.example.travel.domain.repository.TravelRepository
import javax.inject.Inject

class UpdateExpenseUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    suspend operator fun invoke(expense: Expense): Resource<Long> {
        return travelRepository.addExpense(expense)
    }
}
