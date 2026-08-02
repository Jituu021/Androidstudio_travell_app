package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.BudgetDao
import com.example.travel.data.local.db.entity.TripBudgetEntity
import com.example.travel.domain.model.BudgetPrediction
import com.example.travel.domain.model.TripBudget
import com.example.travel.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BudgetRepositoryImpl @Inject constructor(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllTripBudgets(): Flow<List<TripBudget>> {
        return budgetDao.getAllTripBudgets().map { list ->
            list.map { b ->
                TripBudget(
                    id = b.id,
                    tripName = b.tripName,
                    totalBudget = b.totalBudget,
                    currencySymbol = b.currencySymbol,
                    totalDays = b.totalDays,
                    timestamp = b.timestamp
                )
            }
        }
    }

    override suspend fun createTripBudget(budget: TripBudget): Resource<Boolean> {
        return try {
            val entity = TripBudgetEntity(
                id = budget.id.ifEmpty { "budget_${System.currentTimeMillis()}" },
                tripName = budget.tripName,
                totalBudget = budget.totalBudget,
                currencySymbol = budget.currencySymbol,
                totalDays = budget.totalDays
            )
            budgetDao.insertTripBudget(entity)
            Timber.d("Created trip budget: ${budget.tripName}")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error creating trip budget")
            Resource.Error(e.message ?: "Failed to create trip budget", e)
        }
    }

    override suspend fun deleteTripBudget(id: String): Resource<Boolean> {
        return try {
            budgetDao.deleteTripBudget(id)
            Timber.d("Deleted trip budget: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting trip budget")
            Resource.Error(e.message ?: "Failed to delete trip budget", e)
        }
    }

    override suspend fun predictBudgetExceedance(
        totalBudget: Double,
        totalSpent: Double,
        daysElapsed: Int,
        totalTripDays: Int
    ): BudgetPrediction {
        val safeElapsed = daysElapsed.coerceAtLeast(1)
        val safeTotalDays = totalTripDays.coerceAtLeast(1)
        val dailyBurnRate = totalSpent / safeElapsed
        val projectedTotalSpent = dailyBurnRate * safeTotalDays
        val isExceeding = projectedTotalSpent > totalBudget
        val overspend = (projectedTotalSpent - totalBudget).coerceAtLeast(0.0)
        val recommendedDaily = totalBudget / safeTotalDays

        val recommendation = if (isExceeding) {
            "⚠️ At your current burn rate of ₹${dailyBurnRate.toInt()}/day, you are projected to exceed budget by ₹${overspend.toInt()}. Consider switching to budget dining or public transit."
        } else {
            "✅ Excellent financial management! Your spending is well within the target daily limit of ₹${recommendedDaily.toInt()}."
        }

        return BudgetPrediction(
            isExceedingBudget = isExceeding,
            projectedTotalSpent = projectedTotalSpent,
            projectedOverspendAmount = overspend,
            dailyBurnRate = dailyBurnRate,
            recommendedDailyLimit = recommendedDaily,
            savingRecommendation = recommendation
        )
    }
}
