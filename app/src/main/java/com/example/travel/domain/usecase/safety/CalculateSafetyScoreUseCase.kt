package com.example.travel.domain.usecase.safety

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SafetyReport
import com.example.travel.domain.repository.SafetyRepository
import javax.inject.Inject

class CalculateSafetyScoreUseCase @Inject constructor(
    private val safetyRepository: SafetyRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<SafetyReport> {
        return safetyRepository.calculateSafetyScore(lat, lon)
    }
}
