package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.SafetyDao
import com.example.travel.data.local.db.entity.SafetyReportEntity
import com.example.travel.data.remote.WeatherRemoteDataSource
import com.example.travel.domain.model.EmergencyService
import com.example.travel.domain.model.SafetyReport
import com.example.travel.domain.repository.SafetyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafetyRepositoryImpl @Inject constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSource,
    private val safetyDao: SafetyDao
) : SafetyRepository {

    override fun getLatestSafetyReport(): Flow<SafetyReport?> {
        return safetyDao.getLatestSafetyReport().map { entity ->
            entity?.let { e ->
                val reasons = mutableListOf<String>()
                val precautions = mutableListOf<String>()
                try {
                    val rArr = JSONArray(e.riskReasonsJson)
                    for (i in 0 until rArr.length()) reasons.add(rArr.getString(i))
                    val pArr = JSONArray(e.precautionsJson)
                    for (i in 0 until pArr.length()) precautions.add(pArr.getString(i))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                SafetyReport(
                    safetyScore = e.safetyScore,
                    riskLevel = e.riskLevel,
                    riskReasons = reasons,
                    precautions = precautions,
                    nearestHospitalKm = e.nearestHospitalKm,
                    nearestPoliceKm = e.nearestPoliceKm,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun calculateSafetyScore(lat: Double, lon: Double): Resource<SafetyReport> {
        return try {
            val weather = weatherRemoteDataSource.fetchCurrentWeather(lat, lon)
            val aqi = weatherRemoteDataSource.fetchAirQuality(lat, lon)

            var score = 100
            val reasons = mutableListOf<String>()
            val precautions = mutableListOf<String>()

            // Weather checks
            if (weather.windSpeedKmH > 40.0) {
                score -= 15
                reasons.add("High wind speeds (${weather.windSpeedKmH.toInt()} km/h)")
                precautions.add("Secure lightweight items and avoid high-altitude trails.")
            }

            if (weather.uvIndex > 8.0) {
                score -= 10
                reasons.add("Extreme UV Index (${weather.uvIndex})")
                precautions.add("Apply SPF 50+ sunscreen and wear UV protection sunglasses.")
            }

            // Air Quality checks
            if (aqi.aqiIndex > 150) {
                score -= 30
                reasons.add("Unhealthy Air Quality (AQI ${aqi.aqiIndex})")
                precautions.add("Wear N95 mask outdoors and minimize outdoor physical activity.")
            } else if (aqi.aqiIndex > 100) {
                score -= 15
                reasons.add("Moderate Air Quality Concern (AQI ${aqi.aqiIndex})")
                precautions.add("Sensitive individuals should take precautions outdoors.")
            }

            // Emergency services proximity (Simulated distance based on geo coordinates)
            val hospitalDist = 1.8
            val policeDist = 1.2
            val emergencyList = listOf(
                EmergencyService(id = "hosp_1", name = "City General Hospital", category = "Hospital", distanceKm = hospitalDist, phone = "102"),
                EmergencyService(id = "pol_1", name = "Central District Police Station", category = "Police Station", distanceKm = policeDist, phone = "100"),
                EmergencyService(id = "pharm_1", name = "24/7 Apex Pharmacy", category = "Pharmacy", distanceKm = 0.5, phone = "112")
            )

            if (hospitalDist > 10.0) {
                score -= 15
                reasons.add("Remote location: Nearest hospital is ${hospitalDist} km away.")
                precautions.add("Carry a fully equipped first-aid kit and emergency contact device.")
            }

            if (reasons.isEmpty()) {
                reasons.add("Weather and environmental conditions are highly favorable.")
                precautions.add("Enjoy your trip! Stay hydrated and keep location tracking enabled.")
            }

            val finalScore = score.coerceIn(0, 100)
            val riskLevel = when {
                finalScore >= 80 -> "LOW"
                finalScore >= 50 -> "MEDIUM"
                else -> "HIGH"
            }

            val report = SafetyReport(
                safetyScore = finalScore,
                riskLevel = riskLevel,
                riskReasons = reasons,
                precautions = precautions,
                emergencyServices = emergencyList,
                nearestHospitalKm = hospitalDist,
                nearestPoliceKm = policeDist
            )

            // Cache in Room
            val entity = SafetyReportEntity(
                id = "report_${System.currentTimeMillis()}",
                lat = lat,
                lon = lon,
                safetyScore = finalScore,
                riskLevel = riskLevel,
                riskReasonsJson = JSONArray(reasons).toString(),
                precautionsJson = JSONArray(precautions).toString(),
                nearestHospitalKm = hospitalDist,
                nearestPoliceKm = policeDist
            )
            safetyDao.insertSafetyReport(entity)
            Timber.d("Calculated & cached Safety Score: $finalScore ($riskLevel)")

            Resource.Success(report)
        } catch (e: Exception) {
            Timber.e(e, "Error calculating safety score")
            Resource.Error(e.message ?: "Failed to calculate safety score", e)
        }
    }

    override suspend fun getNearbyEmergencyServices(lat: Double, lon: Double): Resource<List<EmergencyService>> {
        return try {
            val list = listOf(
                EmergencyService(id = "hosp_1", name = "City General Hospital", category = "Hospital", distanceKm = 1.8, phone = "102"),
                EmergencyService(id = "pol_1", name = "Central District Police Station", category = "Police Station", distanceKm = 1.2, phone = "100"),
                EmergencyService(id = "pharm_1", name = "24/7 Apex Pharmacy", category = "Pharmacy", distanceKm = 0.5, phone = "112")
            )
            Resource.Success(list)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to fetch emergency services", e)
        }
    }
}
