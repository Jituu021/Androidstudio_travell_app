package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.model.Settlement
import com.example.travel.domain.model.TripMember
import kotlinx.coroutines.flow.Flow

interface GroupExpenseRepository {
    fun getExpensesForTrip(tripId: String): Flow<List<GroupExpense>>
    fun getSettlementsForTrip(tripId: String): Flow<List<Settlement>>
    suspend fun addGroupExpense(expense: GroupExpense): Resource<Boolean>
    suspend fun settlePayment(settlementId: String, isSettled: Boolean): Resource<Boolean>
    suspend fun calculateMinimumCashFlowSettlements(tripId: String, members: List<TripMember>, expenses: List<GroupExpense>): Resource<List<Settlement>>
}
