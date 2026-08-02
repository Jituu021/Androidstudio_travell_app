package com.example.travel.domain.usecase.groupexpense

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.model.Settlement
import com.example.travel.domain.model.TripMember
import com.example.travel.domain.repository.GroupExpenseRepository
import javax.inject.Inject

class CalculateSettlementUseCase @Inject constructor(
    private val groupExpenseRepository: GroupExpenseRepository
) {
    suspend operator fun invoke(tripId: String, members: List<TripMember>, expenses: List<GroupExpense>): Resource<List<Settlement>> {
        return groupExpenseRepository.calculateMinimumCashFlowSettlements(tripId, members, expenses)
    }
}
