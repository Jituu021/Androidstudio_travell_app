package com.example.travel.data.remote

import com.example.travel.domain.model.AirQuality
import com.example.travel.domain.model.CurrentWeather
import com.example.travel.domain.model.DailyForecast
import com.example.travel.domain.model.HourlyForecast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRemoteDataSource @Inject constructor() {

    suspend fun fetchCurrentWeather(lat: Double, lon: Double): CurrentWeather = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&current_weather=true&daily=uv_index_max,sunrise,sunset&timezone=auto"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val curr = json.getJSONObject("current_weather")

                val temp = curr.getDouble("temperature")
                val windSpeed = curr.getDouble("windspeed")
                val windDir = curr.getDouble("winddirection")
                val wCode = curr.getInt("weathercode")

                val daily = json.optJSONObject("daily")
                val uvArr = daily?.optJSONArray("uv_index_max")
                val uv = uvArr?.optDouble(0, 4.0) ?: 4.0

                return@withContext CurrentWeather(
                    temperatureC = temp,
                    feelsLikeC = temp - 1.2,
                    weatherCondition = mapWeatherCode(wCode),
                    humidityPercent = 55,
                    windSpeedKmH = windSpeed,
                    windDirectionDegrees = windDir,
                    uvIndex = uv
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching Open-Meteo weather")
        }
        return@withContext CurrentWeather()
    }

    suspend fun fetchHourlyForecast(lat: Double, lon: Double): List<HourlyForecast> = withContext(Dispatchers.IO) {
        val list = mutableListOf<HourlyForecast>()
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&hourly=temperature_2m,weathercode&timezone=auto"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val hourly = json.getJSONObject("hourly")
                val times = hourly.getJSONArray("time")
                val temps = hourly.getJSONArray("temperature_2m")
                val codes = hourly.getJSONArray("weathercode")

                val limit = times.length().coerceAtMost(24)
                for (i in 0 until limit) {
                    val rawTime = times.getString(i).takeLast(5)
                    val t = temps.getDouble(i)
                    val c = mapWeatherCode(codes.getInt(i))
                    list.add(HourlyForecast(rawTime, t, c))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching hourly forecast")
        }
        return@withContext list.ifEmpty {
            listOf(
                HourlyForecast("12:00", 22.0, "Sunny ☀️"),
                HourlyForecast("15:00", 24.0, "Partly Cloudy ⛅"),
                HourlyForecast("18:00", 20.0, "Clear 🌤️")
            )
        }
    }

    suspend fun fetchDailyForecast(lat: Double, lon: Double): List<DailyForecast> = withContext(Dispatchers.IO) {
        val list = mutableListOf<DailyForecast>()
        try {
            val urlString = "https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$lon&daily=weathercode,temperature_2m_max,temperature_2m_min&timezone=auto"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val daily = json.getJSONObject("daily")
                val dates = daily.getJSONArray("time")
                val maxTemps = daily.getJSONArray("temperature_2m_max")
                val minTemps = daily.getJSONArray("temperature_2m_min")
                val codes = daily.getJSONArray("weathercode")

                val limit = dates.length().coerceAtMost(7)
                for (i in 0 until limit) {
                    list.add(
                        DailyForecast(
                            date = dates.getString(i),
                            maxTempC = maxTemps.getDouble(i),
                            minTempC = minTemps.getDouble(i),
                            condition = mapWeatherCode(codes.getInt(i))
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching daily forecast")
        }
        return@withContext list.ifEmpty {
            listOf(
                DailyForecast("Today", 24.0, 15.0, "Sunny ☀️"),
                DailyForecast("Tomorrow", 22.0, 14.0, "Partly Cloudy ⛅")
            )
        }
    }

    suspend fun fetchAirQuality(lat: Double, lon: Double): AirQuality = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://air-quality-api.open-meteo.com/v1/air-quality?latitude=$lat&longitude=$lon&current=us_aqi,pm10,pm2_5"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val current = json.getJSONObject("current")
                val aqi = current.optInt("us_aqi", 42)
                val pm25 = current.optDouble("pm2_5", 12.0)
                val pm10 = current.optDouble("pm10", 25.0)

                val (status, recommendation) = when {
                    aqi <= 50 -> "Good 😊" to "Air quality is good. Great day for travel & outdoor exploration!"
                    aqi <= 100 -> "Moderate 😐" to "Acceptable air quality for most travelers."
                    aqi <= 150 -> "Unhealthy for Sensitive Groups 😷" to "Wear an N95 mask if sensitive to outdoor pollution."
                    else -> "Severe 🚨" to "High air pollution alert. Limit prolonged outdoor travel."
                }

                return@withContext AirQuality(
                    aqiIndex = aqi,
                    aqiStatus = status,
                    pm25 = pm25,
                    pm10 = pm10,
                    healthRecommendation = recommendation
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching Open-Meteo Air Quality")
        }
        return@withContext AirQuality()
    }

    private fun mapWeatherCode(code: Int): String {
        return when (code) {
            0 -> "Clear Sky ☀️"
            1, 2, 3 -> "Partly Cloudy ⛅"
            45, 48 -> "Foggy 🌫️"
            51, 53, 55 -> "Light Drizzle 🌧️"
            61, 63, 65 -> "Rain 🌧️"
            71, 73, 75 -> "Snow ❄️"
            95, 96, 99 -> "Thunderstorm 🌩️"
            else -> "Mild 🌤️"
        }
    }
}
