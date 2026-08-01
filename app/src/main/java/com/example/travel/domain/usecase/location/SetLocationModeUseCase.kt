package com.example.travel.domain.usecase.location

import com.example.travel.core.location.LocationMode
import com.example.travel.domain.repository.LocationRepository
import javax.inject.Inject

class SetLocationModeUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    operator fun invoke(mode: LocationMode) {
        locationRepository.setLocationMode(mode)
    }
}
