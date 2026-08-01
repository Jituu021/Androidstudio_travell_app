package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.GisRepository
import com.example.travel.gis.data.local.TileDownloader
import com.example.travel.gis.domain.model.LocationTelemetry
import com.example.travel.gis.domain.model.MapLocation
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.OfflineRegion
import com.example.travel.gis.domain.model.TravelMode
import com.example.travel.gis.provider.location.FusedLocationServiceImpl
import com.example.travel.gis.provider.route.OsrmRouteEngine
import com.example.travel.gis.provider.search.GoogleSearchEngine
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GisRepositoryImpl @Inject constructor(
    private val searchEngine: GoogleSearchEngine,
    private val routeEngine: OsrmRouteEngine,
    private val tileDownloader: TileDownloader,
    private val locationService: FusedLocationServiceImpl
) : GisRepository {

    override suspend fun searchPlaces(
        query: String,
        userLat: Double,
        userLon: Double
    ): Resource<List<MapLocation>> {
        return try {
            val places = searchEngine.searchPlaces(query, userLat, userLon)
            Timber.d("Search returned ${places.size} places for query: $query")
            Resource.Success(places)
        } catch (e: Exception) {
            Timber.e(e, "Error searching places")
            Resource.Error(e.message ?: "Failed to search places", e)
        }
    }

    override suspend fun calculateRoute(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        profile: String
    ): Resource<MapRoute> {
        return try {
            val mode = when (profile.lowercase()) {
                "walking", "foot" -> TravelMode.WALKING
                "cycling", "bike" -> TravelMode.CYCLING
                "transit" -> TravelMode.TRANSIT
                else -> TravelMode.DRIVING
            }
            val route = routeEngine.calculateRoute(originLat, originLon, destLat, destLon, mode)
            if (route != null) {
                Timber.d("Route calculated successfully: ${route.totalDistanceMeters}m")
                Resource.Success(route)
            } else {
                Resource.Error("Route calculation failed")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error calculating route")
            Resource.Error(e.message ?: "Failed to calculate route", e)
        }
    }

    override suspend fun downloadOfflineTiles(region: OfflineRegion): Resource<Boolean> {
        return try {
            val result = tileDownloader.downloadRegion(region)
            Timber.d("Offline map tiles download result: $result")
            Resource.Success(result)
        } catch (e: Exception) {
            Timber.e(e, "Error downloading map tiles")
            Resource.Error(e.message ?: "Failed to download offline tiles", e)
        }
    }

    override fun getCurrentLocation(): Flow<LocationTelemetry> {
        return locationService.locationUpdates
    }
}
