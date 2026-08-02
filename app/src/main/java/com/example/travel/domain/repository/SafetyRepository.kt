package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.EmergencyService
import com.example.travel.domain.model.SafetyReport
import kotlinx.coroutines.flow.Flow

interface SafetyRepository {
    fun getLatestSafetyReport(): Flow<SafetyReport?>
    suspend fun calculateSafetyScore(lat: Double, lon: Double): Resource<SafetyReport>
    suspend fun getNearbyEmergencyServices(lat: Double, lon: Double): Resource<List<EmergencyService>>
}
