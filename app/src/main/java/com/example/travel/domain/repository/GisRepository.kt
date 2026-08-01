package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.gis.domain.model.LocationTelemetry
import com.example.travel.gis.domain.model.MapLocation
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.OfflineRegion
import kotlinx.coroutines.flow.Flow

interface GisRepository {
    suspend fun searchPlaces(query: String, userLat: Double, userLon: Double): Resource<List<MapLocation>>
    suspend fun calculateRoute(originLat: Double, originLon: Double, destLat: Double, destLon: Double, profile: String): Resource<MapRoute>
    suspend fun downloadOfflineTiles(region: OfflineRegion): Resource<Boolean>
    fun getCurrentLocation(): Flow<LocationTelemetry>
}
