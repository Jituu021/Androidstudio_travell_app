package com.example.travel.domain.usecase.travel

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.TravelRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    suspend operator fun invoke(id: Long): Resource<Unit> {
        return travelRepository.deleteExpense(id)
    }
}
