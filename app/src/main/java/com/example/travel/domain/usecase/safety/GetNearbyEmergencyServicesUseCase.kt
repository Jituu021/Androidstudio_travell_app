package com.example.travel.domain.usecase.safety

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.EmergencyService
import com.example.travel.domain.repository.SafetyRepository
import javax.inject.Inject

class GetNearbyEmergencyServicesUseCase @Inject constructor(
    private val safetyRepository: SafetyRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<List<EmergencyService>> {
        return safetyRepository.getNearbyEmergencyServices(lat, lon)
    }
}
