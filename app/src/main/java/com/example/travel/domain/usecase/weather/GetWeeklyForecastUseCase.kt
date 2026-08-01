package com.example.travel.domain.usecase.weather

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.DailyForecast
import com.example.travel.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeeklyForecastUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Resource<List<DailyForecast>> {
        return weatherRepository.getWeeklyForecast(lat, lon)
    }
}
