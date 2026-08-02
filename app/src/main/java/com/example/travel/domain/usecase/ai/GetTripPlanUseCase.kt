package com.example.travel.domain.usecase.ai

import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.repository.AITripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripPlanUseCase @Inject constructor(
    private val aiTripRepository: AITripRepository
) {
    operator fun invoke(): Flow<List<AiTripPlan>> {
        return aiTripRepository.getAllSavedTripPlans()
    }
}
