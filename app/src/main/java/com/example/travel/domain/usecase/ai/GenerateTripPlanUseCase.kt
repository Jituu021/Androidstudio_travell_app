package com.example.travel.domain.usecase.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.repository.AITripRepository
import javax.inject.Inject

class GenerateTripPlanUseCase @Inject constructor(
    private val aiTripRepository: AITripRepository
) {
    suspend operator fun invoke(
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        travelers: Int,
        travelStyle: String,
        interests: List<String>
    ): Resource<AiTripPlan> {
        return aiTripRepository.generateTripPlan(
            destination, startDate, endDate, budget, travelers, travelStyle, interests
        )
    }
}
