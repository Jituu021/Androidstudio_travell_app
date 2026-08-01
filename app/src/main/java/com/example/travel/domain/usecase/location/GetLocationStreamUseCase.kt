package com.example.travel.domain.usecase.location

import com.example.travel.domain.repository.LocationRepository
import com.example.travel.gis.domain.model.LocationTelemetry
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationStreamUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(): Flow<LocationTelemetry> {
        return locationRepository.getLocationStream()
    }
}
