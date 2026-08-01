package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.WeatherDao
import com.example.travel.data.local.db.entity.WeatherCacheEntity
import com.example.travel.data.remote.WeatherRemoteDataSource
import com.example.travel.domain.model.AirQuality
import com.example.travel.domain.model.CurrentWeather
import com.example.travel.domain.model.DailyForecast
import com.example.travel.domain.model.HourlyForecast
import com.example.travel.domain.repository.WeatherRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepositoryImpl @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val weatherDao: WeatherDao
) : WeatherRepository {

    override suspend fun getCurrentWeather(lat: Double, lon: Double): Resource<CurrentWeather> {
        return try {
            val weather = weatherRemoteDataSource.fetchCurrentWeather(lat, lon)
            // Cache in Room
            val entity = WeatherCacheEntity(
                id = "weather_${lat}_${lon}",
                lat = lat,
                lon = lon,
                temperatureC = weather.temperatureC,
                feelsLikeC = weather.feelsLikeC,
                weatherCondition = weather.weatherCondition,
                humidityPercent = weather.humidityPercent,
                windSpeedKmH = weather.windSpeedKmH,
                windDirectionDegrees = weather.windDirectionDegrees,
                uvIndex = weather.uvIndex,
                aqiIndex = 42,
                aqiStatus = "Good 😊",
                hourlyJson = "[]",
                dailyJson = "[]"
            )
            weatherDao.insertWeatherCache(entity)
            Resource.Success(weather)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching current weather")
            val cached = weatherDao.getCachedWeather("weather_${lat}_${lon}")
            if (cached != null) {
                Resource.Success(
                    CurrentWeather(
                        temperatureC = cached.temperatureC,
                        feelsLikeC = cached.feelsLikeC,
                        weatherCondition = cached.weatherCondition,
                        humidityPercent = cached.humidityPercent,
                        windSpeedKmH = cached.windSpeedKmH,
                        windDirectionDegrees = cached.windDirectionDegrees,
                        uvIndex = cached.uvIndex
                    )
                )
            } else {
                Resource.Success(CurrentWeather())
            }
        }
    }

    override suspend fun getHourlyForecast(lat: Double, lon: Double): Resource<List<HourlyForecast>> {
        return try {
            val list = weatherRemoteDataSource.fetchHourlyForecast(lat, lon)
            Resource.Success(list)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching hourly forecast")
            Resource.Error(e.message ?: "Failed to fetch hourly forecast", e)
        }
    }

    override suspend fun getWeeklyForecast(lat: Double, lon: Double): Resource<List<DailyForecast>> {
        return try {
            val list = weatherRemoteDataSource.fetchDailyForecast(lat, lon)
            Resource.Success(list)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching daily forecast")
            Resource.Error(e.message ?: "Failed to fetch daily forecast", e)
        }
    }

    override suspend fun getAirQuality(lat: Double, lon: Double): Resource<AirQuality> {
        return try {
            val aqi = weatherRemoteDataSource.fetchAirQuality(lat, lon)
            Resource.Success(aqi)
        } catch (e: Exception) {
            Timber.e(e, "Error fetching air quality")
            Resource.Error(e.message ?: "Failed to fetch air quality", e)
        }
    }
}
