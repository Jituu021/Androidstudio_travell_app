package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.SafetyReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SafetyDao {
    @Query("SELECT * FROM safety_reports ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSafetyReport(): Flow<SafetyReportEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSafetyReport(report: SafetyReportEntity)

    @Query("DELETE FROM safety_reports")
    suspend fun clearSafetyReports()
}
