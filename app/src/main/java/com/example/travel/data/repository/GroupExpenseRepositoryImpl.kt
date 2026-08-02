package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.GroupExpenseDao
import com.example.travel.data.local.db.entity.GroupExpenseEntity
import com.example.travel.data.local.db.entity.SettlementEntity
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.model.Settlement
import com.example.travel.domain.model.TripMember
import com.example.travel.domain.repository.GroupExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupExpenseRepositoryImpl @Inject constructor(
    private val groupExpenseDao: GroupExpenseDao
) : GroupExpenseRepository {

    override fun getExpensesForTrip(tripId: String): Flow<List<GroupExpense>> {
        return groupExpenseDao.getExpensesForTrip(tripId).map { list ->
            list.map { e ->
                GroupExpense(
                    id = e.id,
                    tripId = e.tripId,
                    paidByUserId = e.paidByUserId,
                    paidByName = e.paidByName,
                    amount = e.amount,
                    title = e.title,
                    category = e.category,
                    splitMethod = e.splitMethod,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getSettlementsForTrip(tripId: String): Flow<List<Settlement>> {
        return groupExpenseDao.getSettlementsForTrip(tripId).map { list ->
            list.map { e ->
                Settlement(
                    id = e.id,
                    tripId = e.tripId,
                    payerId = e.payerId,
                    payerName = e.payerName,
                    payeeId = e.payeeId,
                    payeeName = e.payeeName,
                    amount = e.amount,
                    isSettled = e.isSettled,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun addGroupExpense(expense: GroupExpense): Resource<Boolean> {
        return try {
            val entity = GroupExpenseEntity(
                id = expense.id.ifEmpty { "gexp_${System.currentTimeMillis()}" },
                tripId = expense.tripId,
                paidByUserId = expense.paidByUserId,
                paidByName = expense.paidByName,
                amount = expense.amount,
                title = expense.title,
                category = expense.category,
                splitMethod = expense.splitMethod
            )
            groupExpenseDao.insertGroupExpense(entity)
            Timber.d("Added group expense: ${expense.title} (₹${expense.amount})")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error adding group expense")
            Resource.Error(e.message ?: "Failed to add group expense", e)
        }
    }

    override suspend fun settlePayment(settlementId: String, isSettled: Boolean): Resource<Boolean> {
        return try {
            groupExpenseDao.updateSettledStatus(settlementId, isSettled)
            Timber.d("Updated settlement $settlementId status to $isSettled")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update settlement", e)
        }
    }

    override suspend fun calculateMinimumCashFlowSettlements(
        tripId: String,
        members: List<TripMember>,
        expenses: List<GroupExpense>
    ): Resource<List<Settlement>> {
        return try {
            if (members.isEmpty()) return Resource.Success(emptyList())

            val memberMap = members.associateBy { it.userId }
            val netBalance = mutableMapOf<String, Double>()
            members.forEach { netBalance[it.userId] = 0.0 }

            expenses.forEach { exp ->
                val payer = exp.paidByUserId
                val amount = exp.amount
                val share = amount / members.size.coerceAtLeast(1)

                netBalance[payer] = (netBalance[payer] ?: 0.0) + amount
                members.forEach { m ->
                    netBalance[m.userId] = (netBalance[m.userId] ?: 0.0) - share
                }
            }

            // Minimum Cash Flow Greedy Solver
            val resultSettlements = mutableListOf<Settlement>()
            val balances = netBalance.toMutableMap()

            while (true) {
                val maxCreditUser = balances.maxByOrNull { it.value }?.key
                val maxDebitUser = balances.minByOrNull { it.value }?.key

                if (maxCreditUser == null || maxDebitUser == null) break

                val maxCredit = balances[maxCreditUser] ?: 0.0
                val maxDebit = balances[maxDebitUser] ?: 0.0

                if (maxCredit < 0.01 && maxDebit > -0.01) break // Fully settled

                val minAmount = kotlin.math.min(maxCredit, -maxDebit)
                balances[maxCreditUser] = maxCredit - minAmount
                balances[maxDebitUser] = maxDebit + minAmount

                val payerName = memberMap[maxDebitUser]?.name ?: "Member"
                val payeeName = memberMap[maxCreditUser]?.name ?: "Member"

                resultSettlements.add(
                    Settlement(
                        id = "stl_${tripId}_${maxDebitUser}_${maxCreditUser}",
                        tripId = tripId,
                        payerId = maxDebitUser,
                        payerName = payerName,
                        payeeId = maxCreditUser,
                        payeeName = payeeName,
                        amount = minAmount
                    )
                )
            }

            // Save to Room DB
            groupExpenseDao.clearSettlementsForTrip(tripId)
            val entities = resultSettlements.map { s ->
                SettlementEntity(
                    id = s.id,
                    tripId = s.tripId,
                    payerId = s.payerId,
                    payerName = s.payerName,
                    payeeId = s.payeeId,
                    payeeName = s.payeeName,
                    amount = s.amount,
                    isSettled = s.isSettled
                )
            }
            groupExpenseDao.insertAllSettlements(entities)
            Timber.d("Calculated ${resultSettlements.size} minimum cash flow settlements for trip $tripId")
            Resource.Success(resultSettlements)
        } catch (e: Exception) {
            Timber.e(e, "Error calculating minimum cash flow settlements")
            Resource.Error(e.message ?: "Failed to calculate settlements", e)
        }
    }
}
