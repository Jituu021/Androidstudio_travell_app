package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.GroupExpenseEntity
import com.example.travel.data.local.db.entity.SettlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroupExpenseDao {
    @Query("SELECT * FROM group_expenses WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getExpensesForTrip(tripId: String): Flow<List<GroupExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroupExpense(expense: GroupExpenseEntity)

    @Query("SELECT * FROM settlements WHERE tripId = :tripId")
    fun getSettlementsForTrip(tripId: String): Flow<List<SettlementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettlement(settlement: SettlementEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllSettlements(settlements: List<SettlementEntity>)

    @Query("UPDATE settlements SET isSettled = :isSettled WHERE id = :id")
    suspend fun updateSettledStatus(id: String, isSettled: Boolean)

    @Query("DELETE FROM settlements WHERE tripId = :tripId")
    suspend fun clearSettlementsForTrip(tripId: String)
}
