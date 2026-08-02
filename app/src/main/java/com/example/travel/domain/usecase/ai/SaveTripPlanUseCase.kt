package com.example.travel.domain.usecase.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.repository.AITripRepository
import javax.inject.Inject

class SaveTripPlanUseCase @Inject constructor(
    private val aiTripRepository: AITripRepository
) {
    suspend operator fun invoke(plan: AiTripPlan): Resource<Boolean> {
        return aiTripRepository.saveTripPlan(plan)
    }
}
