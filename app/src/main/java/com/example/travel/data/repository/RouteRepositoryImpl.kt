package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.RouteDao
import com.example.travel.data.local.db.entity.RouteCacheEntity
import com.example.travel.data.remote.RouteRemoteDataSource
import com.example.travel.domain.repository.RouteRepository
import com.example.travel.gis.domain.model.MapLocation
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.RouteStep
import com.example.travel.gis.domain.model.TravelMode
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val routeRemoteDataSource: RouteRemoteDataSource,
    private val routeDao: RouteDao
) : RouteRepository {

    override suspend fun calculateRoute(
        waypoints: List<Pair<Double, Double>>,
        mode: TravelMode
    ): Resource<List<MapRoute>> {
        return try {
            if (waypoints.size < 2) {
                return Resource.Error("Minimum 2 waypoints required for routing")
            }

            var routes = routeRemoteDataSource.fetchOsrmRoutes(waypoints, mode)
            if (routes.isNotEmpty()) {
                val primary = routes.first()
                val cacheKey = buildCacheKey(waypoints, mode)
                val pointsJson = JSONArray().apply {
                    primary.polylinePoints.forEach { pt ->
                        put(JSONObject().apply {
                            put("lat", pt.first)
                            put("lon", pt.second)
                        })
                    }
                }.toString()

                val cacheEntity = RouteCacheEntity(
                    id = cacheKey,
                    originLat = waypoints.first().first,
                    originLon = waypoints.first().second,
                    destLat = waypoints.last().first,
                    destLon = waypoints.last().second,
                    travelMode = mode.name,
                    distanceMeters = primary.totalDistanceMeters,
                    durationSeconds = primary.totalDurationSeconds.toDouble(),
                    polylinePointsJson = pointsJson,
                    stepsJson = "[]"
                )
                routeDao.insertRouteCache(cacheEntity)
                Timber.d("Fetched and cached ${routes.size} routes for mode $mode")
                Resource.Success(routes)
            } else {
                val fallback = generateFallbackRoute(waypoints, mode)
                Resource.Success(listOf(fallback))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error calculating route")
            val cacheKey = buildCacheKey(waypoints, mode)
            val cached = routeDao.getCachedRoute(cacheKey)
            if (cached != null) {
                val polyPoints = parsePolylinePointsJson(cached.polylinePointsJson)
                val originLoc = MapLocation(
                    id = "cached_orig",
                    name = "Start Point (Cached)",
                    latitude = cached.originLat,
                    longitude = cached.originLon
                )
                val destLoc = MapLocation(
                    id = "cached_dest",
                    name = "Destination (Cached)",
                    latitude = cached.destLat,
                    longitude = cached.destLon
                )
                val offlineRoute = MapRoute(
                    id = cached.id,
                    origin = originLoc,
                    destination = destLoc,
                    totalDistanceText = formatDistance(cached.distanceMeters),
                    totalDistanceMeters = cached.distanceMeters,
                    totalDurationText = formatDuration(cached.durationSeconds),
                    totalDurationSeconds = cached.durationSeconds.toLong(),
                    polylinePoints = polyPoints,
                    steps = emptyList(),
                    travelMode = mode
                )
                Resource.Success(listOf(offlineRoute))
            } else {
                val fallback = generateFallbackRoute(waypoints, mode)
                Resource.Success(listOf(fallback))
            }
        }
    }

    override fun optimizeWaypointOrder(waypoints: List<Pair<Double, Double>>): List<Pair<Double, Double>> {
        if (waypoints.size <= 2) return waypoints
        val unvisited = waypoints.drop(1).toMutableList()
        val optimized = mutableListOf<Pair<Double, Double>>()
        optimized.add(waypoints.first())

        var current = waypoints.first()
        while (unvisited.isNotEmpty()) {
            val nearest = unvisited.minByOrNull { pt ->
                calculateDistanceMeters(current.first, current.second, pt.first, pt.second)
            } ?: unvisited.first()

            optimized.add(nearest)
            unvisited.remove(nearest)
            current = nearest
        }
        return optimized
    }

    private fun generateFallbackRoute(waypoints: List<Pair<Double, Double>>, mode: TravelMode): MapRoute {
        val points = mutableListOf<Pair<Double, Double>>()
        var totalDist = 0.0
        for (i in 0 until waypoints.size - 1) {
            val p1 = waypoints[i]
            val p2 = waypoints[i + 1]
            points.add(p1)

            val midLat = (p1.first + p2.first) / 2.0
            val midLon = (p1.second + p2.second) / 2.0
            points.add(Pair(midLat, midLon))

            totalDist += calculateDistanceMeters(p1.first, p1.second, p2.first, p2.second)
        }
        points.add(waypoints.last())

        val speedMs = when (mode) {
            TravelMode.WALKING -> 1.4
            TravelMode.CYCLING -> 4.5
            TravelMode.DRIVING -> 11.0
            TravelMode.TRANSIT -> 10.0
        }
        val durationSec = totalDist / speedMs

        val originLoc = MapLocation(
            id = "fallback_orig",
            name = "Start Point",
            latitude = waypoints.first().first,
            longitude = waypoints.first().second
        )

        val destLoc = MapLocation(
            id = "fallback_dest",
            name = "Destination",
            latitude = waypoints.last().first,
            longitude = waypoints.last().second
        )

        val fallbackStep = RouteStep(
            instruction = "Head towards destination",
            distanceText = formatDistance(totalDist),
            distanceMeters = totalDist,
            durationText = formatDuration(durationSec),
            maneuver = "straight",
            startLat = waypoints.first().first,
            startLon = waypoints.first().second,
            endLat = waypoints.last().first,
            endLon = waypoints.last().second
        )

        return MapRoute(
            id = "fallback_route_${System.currentTimeMillis()}",
            origin = originLoc,
            destination = destLoc,
            totalDistanceText = formatDistance(totalDist),
            totalDistanceMeters = totalDist,
            totalDurationText = formatDuration(durationSec),
            totalDurationSeconds = durationSec.toLong(),
            polylinePoints = points,
            steps = listOf(fallbackStep),
            travelMode = mode
        )
    }

    private fun buildCacheKey(waypoints: List<Pair<Double, Double>>, mode: TravelMode): String {
        val o = waypoints.first()
        val d = waypoints.last()
        return "route_${o.first}_${o.second}_${d.first}_${d.second}_${mode.name}"
    }

    private fun parsePolylinePointsJson(jsonStr: String): List<Pair<Double, Double>> {
        val list = mutableListOf<Pair<Double, Double>>()
        try {
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(Pair(obj.getDouble("lat"), obj.getDouble("lon")))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    private fun formatDistance(meters: Double): String {
        return if (meters >= 1000) String.format("%.1f km", meters / 1000.0)
        else String.format("%d m", meters.toInt())
    }

    private fun formatDuration(seconds: Double): String {
        val mins = (seconds / 60.0).toInt()
        return if (mins >= 60) "${mins / 60}h ${mins % 60}m" else "$mins min"
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
