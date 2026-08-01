package com.example.travel.data.remote

import com.example.travel.gis.domain.model.MapLocation
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.RouteStep
import com.example.travel.gis.domain.model.TravelMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRemoteDataSource @Inject constructor() {

    suspend fun fetchOsrmRoutes(
        waypoints: List<Pair<Double, Double>>,
        mode: TravelMode
    ): List<MapRoute> = withContext(Dispatchers.IO) {
        val routesList = mutableListOf<MapRoute>()
        try {
            if (waypoints.size < 2) return@withContext emptyList()

            val profile = when (mode) {
                TravelMode.DRIVING -> "driving"
                TravelMode.WALKING -> "foot"
                TravelMode.CYCLING -> "bike"
                TravelMode.TRANSIT -> "driving"
            }

            val coordsString = waypoints.joinToString(";") { "${it.second},${it.first}" }
            val urlString = "https://router.project-osrm.org/route/v1/$profile/$coordsString?overview=full&geometries=geojson&steps=true&alternatives=true"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 6000
            conn.readTimeout = 6000

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(jsonText)
                val routesArray = json.optJSONArray("routes") ?: return@withContext emptyList()

                for (rIdx in 0 until routesArray.length()) {
                    val rObj = routesArray.getJSONObject(rIdx)
                    val distanceMeters = rObj.getDouble("distance")
                    val durationSeconds = rObj.getDouble("duration")

                    val geometryObj = rObj.getJSONObject("geometry")
                    val coordsArray = geometryObj.getJSONArray("coordinates")
                    val routeGeoPoints = mutableListOf<Pair<Double, Double>>()
                    for (cIdx in 0 until coordsArray.length()) {
                        val pt = coordsArray.getJSONArray(cIdx)
                        val lon = pt.getDouble(0)
                        val lat = pt.getDouble(1)
                        routeGeoPoints.add(Pair(lat, lon))
                    }

                    val legsArray = rObj.optJSONArray("legs")
                    val stepsList = mutableListOf<RouteStep>()
                    if (legsArray != null) {
                        for (lIdx in 0 until legsArray.length()) {
                            val leg = legsArray.getJSONObject(lIdx)
                            val stepsArr = leg.optJSONArray("steps")
                            if (stepsArr != null) {
                                for (sIdx in 0 until stepsArr.length()) {
                                    val stepObj = stepsArr.getJSONObject(sIdx)
                                    val maneuverObj = stepObj.optJSONObject("maneuver")
                                    val instrText = stepObj.optString("name", "Proceed on route")
                                    val stepDist = stepObj.optDouble("distance", 0.0)
                                    val stepDur = stepObj.optDouble("duration", 0.0)
                                    val mType = maneuverObj?.optString("type", "turn") ?: "turn"

                                    val locationArr = maneuverObj?.optJSONArray("location")
                                    val stepLat = locationArr?.optDouble(1) ?: waypoints.first().first
                                    val stepLon = locationArr?.optDouble(0) ?: waypoints.first().second

                                    stepsList.add(
                                        RouteStep(
                                            instruction = instrText.ifEmpty { "Proceed on route" },
                                            distanceText = formatDistance(stepDist),
                                            distanceMeters = stepDist,
                                            durationText = formatDuration(stepDur),
                                            maneuver = mType,
                                            startLat = stepLat,
                                            startLon = stepLon,
                                            endLat = stepLat,
                                            endLon = stepLon
                                        )
                                    )
                                }
                            }
                        }
                    }

                    val originLoc = MapLocation(
                        id = "orig_${waypoints.first().first}_${waypoints.first().second}",
                        name = "Start Point",
                        latitude = waypoints.first().first,
                        longitude = waypoints.first().second
                    )

                    val destLoc = MapLocation(
                        id = "dest_${waypoints.last().first}_${waypoints.last().second}",
                        name = "Destination Point",
                        latitude = waypoints.last().first,
                        longitude = waypoints.last().second
                    )

                    routesList.add(
                        MapRoute(
                            id = "osrm_route_${rIdx}_${System.currentTimeMillis()}",
                            origin = originLoc,
                            destination = destLoc,
                            totalDistanceText = formatDistance(distanceMeters),
                            totalDistanceMeters = distanceMeters,
                            totalDurationText = formatDuration(durationSeconds),
                            totalDurationSeconds = durationSeconds.toLong(),
                            polylinePoints = routeGeoPoints,
                            steps = stepsList,
                            travelMode = mode
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "OSRM Route Engine exception")
        }
        return@withContext routesList
    }

    private fun formatDistance(meters: Double): String {
        return if (meters >= 1000) String.format("%.1f km", meters / 1000.0)
        else String.format("%d m", meters.toInt())
    }

    private fun formatDuration(seconds: Double): String {
        val mins = (seconds / 60.0).toInt()
        return if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "$mins min"
    }
}
