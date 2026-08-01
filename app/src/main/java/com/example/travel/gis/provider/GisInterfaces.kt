package com.example.travel.gis.provider

import android.content.Context
import com.example.travel.gis.domain.model.*
import kotlinx.coroutines.flow.Flow

interface ILocationService {
    val locationUpdates: Flow<LocationTelemetry>
    fun startTrackingHighAccuracy()
    fun stopTracking()
    fun getLastKnownLocation(): LocationTelemetry?
}

interface IRouteEngine {
    suspend fun calculateRoute(
        originLat: Double,
        originLon: Double,
        destLat: Double,
        destLon: Double,
        mode: TravelMode
    ): MapRoute?
}

interface ISearchEngine {
    suspend fun searchPlaces(query: String, userLat: Double, userLon: Double): List<MapLocation>
    suspend fun searchNearbyPois(category: String, userLat: Double, userLon: Double, radiusMeters: Int = 5000): List<MapLocation>
    suspend fun reverseGeocode(lat: Double, lon: Double): String
}

interface IOfflineMapService {
    val downloadProgress: Flow<Pair<String, Int>> // Region ID to Percent
    suspend fun downloadRegion(region: OfflineRegion): Boolean
    suspend fun getDownloadedRegions(): List<OfflineRegion>
    suspend fun deleteRegion(regionId: String): Boolean
}
