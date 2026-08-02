package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.AiTripPlanEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiTripPlanDao {
    @Query("SELECT * FROM ai_trip_plans ORDER BY timestamp DESC")
    fun getAllTripPlans(): Flow<List<AiTripPlanEntity>>

    @Query("SELECT * FROM ai_trip_plans WHERE id = :id")
    suspend fun getTripPlanById(id: String): AiTripPlanEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripPlan(plan: AiTripPlanEntity)

    @Query("DELETE FROM ai_trip_plans WHERE id = :id")
    suspend fun deleteTripPlan(id: String)
}
