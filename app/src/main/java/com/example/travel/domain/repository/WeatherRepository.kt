package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AirQuality
import com.example.travel.domain.model.CurrentWeather
import com.example.travel.domain.model.DailyForecast
import com.example.travel.domain.model.HourlyForecast

interface WeatherRepository {
    suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<CurrentWeather>
    suspend fun getHourlyForecast(lat: Double, lon: Double): Resource<List<HourlyForecast>>
    suspend fun getWeeklyForecast(lat: Double, lon: Double): Resource<List<DailyForecast>>
    suspend fun getAirQuality(lat: Double, lon: Double): Resource<AirQuality>
}
