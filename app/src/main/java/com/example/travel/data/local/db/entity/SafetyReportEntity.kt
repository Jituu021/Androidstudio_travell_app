package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "safety_reports")
data class SafetyReportEntity(
    @PrimaryKey val id: String,
    val lat: Double,
    val lon: Double,
    val safetyScore: Int,
    val riskLevel: String,
    val riskReasonsJson: String,
    val precautionsJson: String,
    val nearestHospitalKm: Double,
    val nearestPoliceKm: Double,
    val timestamp: Long = System.currentTimeMillis()
)
