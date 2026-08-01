package com.example.travel.domain.usecase.travel

import com.example.travel.domain.repository.TravelRepository
import javax.inject.Inject

class CalculateSunriseSunsetUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    operator fun invoke(lat: Double, lon: Double, dateMillis: Long = System.currentTimeMillis()): Pair<String, String> {
        return travelRepository.calculateSunriseSunset(lat, lon, dateMillis)
    }
}
