package com.example.travel.domain.model

data class CurrentWeather(
    val temperatureC: Double = 22.0,
    val feelsLikeC: Double = 21.5,
    val weatherCondition: String = "Clear Sky ☀️",
    val humidityPercent: Int = 45,
    val windSpeedKmH: Double = 12.0,
    val windDirectionDegrees: Double = 180.0,
    val visibilityMeters: Int = 10000,
    val uvIndex: Double = 4.5,
    val pressureHpa: Double = 1013.2,
    val sunriseTime: String = "06:15 AM",
    val sunsetTime: String = "07:30 PM"
)

data class HourlyForecast(
    val time: String,
    val tempC: Double,
    val condition: String
)

data class DailyForecast(
    val date: String,
    val maxTempC: Double,
    val minTempC: Double,
    val condition: String
)

data class AirQuality(
    val aqiIndex: Int = 42,
    val aqiStatus: String = "Good 😊",
    val pm25: Double = 12.0,
    val pm10: Double = 25.0,
    val healthRecommendation: String = "Air quality is good. Enjoy outdoor travel activities!"
)
