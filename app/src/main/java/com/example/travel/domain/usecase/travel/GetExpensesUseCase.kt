package com.example.travel.domain.usecase.travel

import com.example.travel.domain.model.Expense
import com.example.travel.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    operator fun invoke(): Flow<List<Expense>> {
        return travelRepository.getExpenses()
    }
}
