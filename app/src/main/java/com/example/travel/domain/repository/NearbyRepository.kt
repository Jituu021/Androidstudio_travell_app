package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.flow.Flow

interface NearbyRepository {
    suspend fun getNearbyPlaces(
        category: String,
        userLat: Double,
        userLon: Double,
        radiusMeters: Int = 3000,
        sortBy: String = "distance"
    ): Resource<List<MapLocation>>

    fun getSavedPlaces(): Flow<List<MapLocation>>
    suspend fun savePlace(place: MapLocation): Resource<Boolean>
}
