package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey val id: String,
    val lat: Double,
    val lon: Double,
    val temperatureC: Double,
    val feelsLikeC: Double,
    val weatherCondition: String,
    val humidityPercent: Int,
    val windSpeedKmH: Double,
    val windDirectionDegrees: Double,
    val uvIndex: Double,
    val aqiIndex: Int,
    val aqiStatus: String,
    val hourlyJson: String,
    val dailyJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
