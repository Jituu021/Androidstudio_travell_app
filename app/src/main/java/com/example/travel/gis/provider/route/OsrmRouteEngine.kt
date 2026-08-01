package com.example.travel.gis.provider.route

import com.example.travel.gis.domain.model.*
import com.example.travel.gis.provider.IRouteEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.*

class OsrmRouteEngine : IRouteEngine {

    override suspend fun calculateRoute(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        mode: TravelMode
    ): MapRoute? = withContext(Dispatchers.IO) {
        try {
            val profile = when (mode) {
                TravelMode.DRIVING -> "driving"
                TravelMode.WALKING -> "foot"
                TravelMode.CYCLING -> "bike"
                TravelMode.TRANSIT -> "driving"
            }

            val urlString = "https://router.project-osrm.org/route/v1/$profile/$originLon,$originLat;$destLon,$destLat?overview=full&geometries=geojson&steps=true"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val response = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(response)
                val routes = json.optJSONArray("routes")
                if (routes != null && routes.length() > 0) {
                    val routeObj = routes.getJSONObject(0)
                    val distanceMeters = routeObj.optDouble("distance", 0.0)
                    val durationSec = routeObj.optLong("duration", 0L)

                    val geometry = routeObj.getJSONObject("geometry")
                    val coords = geometry.getJSONArray("coordinates")
                    val polylinePoints = mutableListOf<Pair<Double, Double>>()
                    for (i in 0 until coords.length()) {
                        val point = coords.getJSONArray(i)
                        val lon = point.getDouble(0)
                        val lat = point.getDouble(1)
                        polylinePoints.add(lat to lon)
                    }

                    // Steps
                    val stepsList = mutableListOf<RouteStep>()
                    val legs = routeObj.getJSONArray("legs")
                    if (legs.length() > 0) {
                        val steps = legs.getJSONObject(0).getJSONArray("steps")
                        for (i in 0 until steps.length()) {
                            val step = steps.getJSONObject(i)
                            val stepDist = step.optDouble("distance", 0.0)
                            val stepDuration = step.optDouble("duration", 0.0)
                            val name = step.optString("name", "Road")
                            val maneuver = step.getJSONObject("maneuver")
                            val type = maneuver.optString("type", "turn")
                            val modifier = maneuver.optString("modifier", "straight")

                            val maneuverText = formatManeuverText(type, modifier, name)
                            val location = maneuver.getJSONArray("location")

                            stepsList.add(
                                RouteStep(
                                    instruction = maneuverText,
                                    distanceText = formatDistance(stepDist),
                                    distanceMeters = stepDist,
                                    durationText = "${ceil(stepDuration / 60).toInt()} min",
                                    maneuver = "$type-$modifier",
                                    startLat = location.getDouble(1),
                                    startLon = location.getDouble(0),
                                    endLat = location.getDouble(1),
                                    endLon = location.getDouble(0)
                                )
                            )
                        }
                    }

                    val origin = MapLocation(name = "Current Location", latitude = originLat, longitude = originLon)
                    val destination = MapLocation(name = "Destination", latitude = destLat, longitude = destLon)

                    return@withContext MapRoute(
                        id = "osrm_${System.currentTimeMillis()}",
                        origin = origin,
                        destination = destination,
                        totalDistanceText = formatDistance(distanceMeters),
                        totalDistanceMeters = distanceMeters,
                        totalDurationText = formatDuration(durationSec),
                        totalDurationSeconds = durationSec,
                        polylinePoints = polylinePoints,
                        steps = stepsList,
                        travelMode = mode
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Fallback straight line route computation if OSRM is unreachable
        val distance = calculateHaversineDistance(originLat, originLon, destLat, destLon)
        val speedKmH = when (mode) {
            TravelMode.DRIVING -> 40.0
            TravelMode.WALKING -> 4.5
            TravelMode.CYCLING -> 15.0
            TravelMode.TRANSIT -> 30.0
        }
        val timeHours = (distance / 1000.0) / speedKmH
        val durationSec = (timeHours * 3600).toLong()

        MapRoute(
            id = "fallback_${System.currentTimeMillis()}",
            origin = MapLocation(name = "Origin", latitude = originLat, longitude = originLon),
            destination = MapLocation(name = "Destination", latitude = destLat, longitude = destLon),
            totalDistanceText = formatDistance(distance),
            totalDistanceMeters = distance,
            totalDurationText = formatDuration(durationSec),
            totalDurationSeconds = durationSec,
            polylinePoints = listOf(originLat to originLon, destLat to destLon),
            steps = listOf(
                RouteStep("Head towards destination", formatDistance(distance), distance, formatDuration(durationSec), "straight", originLat, originLon, destLat, destLon)
            ),
            travelMode = mode
        )
    }

    private fun formatManeuverText(type: String, modifier: String, streetName: String): String {
        val street = if (streetName.isNotBlank()) " onto $streetName" else ""
        return when (type) {
            "turn" -> "Turn ${modifier.replace("-", " ")}$street"
            "new name" -> "Continue$street"
            "depart" -> "Head ${modifier.replace("-", " ")}$street"
            "arrive" -> "Arrive at destination"
            "fork" -> "Take the ${modifier.replace("-", " ")} fork$street"
            "roundabout" -> "Enter roundabout and take exit$street"
            else -> "Continue ${modifier.replace("-", " ")}$street"
        }
    }

    private fun formatDistance(meters: Double): String {
        return if (meters >= 1000) {
            String.format("%.1f km", meters / 1000.0)
        } else {
            "${meters.roundToInt()} m"
        }
    }

    private fun formatDuration(seconds: Long): String {
        val mins = (seconds / 60).toInt()
        val hours = mins / 60
        val remMins = mins % 60
        return if (hours > 0) {
            "${hours}h ${remMins}m"
        } else {
            "${max(1, mins)} min"
        }
    }

    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0 // Earth radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
