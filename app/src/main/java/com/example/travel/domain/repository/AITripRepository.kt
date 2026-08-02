package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AiTripPlan
import kotlinx.coroutines.flow.Flow

interface AITripRepository {
    suspend fun generateTripPlan(
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        travelers: Int,
        travelStyle: String,
        interests: List<String>
    ): Resource<AiTripPlan>

    fun getAllSavedTripPlans(): Flow<List<AiTripPlan>>
    suspend fun saveTripPlan(plan: AiTripPlan): Resource<Boolean>
    suspend fun deleteTripPlan(id: String): Resource<Boolean>
}
