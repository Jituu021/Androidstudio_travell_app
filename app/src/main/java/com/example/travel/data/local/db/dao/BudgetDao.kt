package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.TripBudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {
    @Query("SELECT * FROM trip_budgets ORDER BY timestamp DESC")
    fun getAllTripBudgets(): Flow<List<TripBudgetEntity>>

    @Query("SELECT * FROM trip_budgets WHERE id = :id")
    suspend fun getTripBudgetById(id: String): TripBudgetEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripBudget(budget: TripBudgetEntity)

    @Query("DELETE FROM trip_budgets WHERE id = :id")
    suspend fun deleteTripBudget(id: String)
}
