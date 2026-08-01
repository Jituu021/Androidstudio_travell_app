package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.gis.domain.model.OfflineRegion
import kotlinx.coroutines.flow.Flow

interface OfflineMapRepository {
    fun getAllOfflineRegions(): Flow<List<OfflineRegion>>
    suspend fun downloadOfflineRegion(
        regionName: String,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double,
        minZoom: Int = 10,
        maxZoom: Int = 14
    ): Resource<Boolean>
    suspend fun deleteOfflineRegion(id: String): Resource<Boolean>
    suspend fun renameOfflineRegion(id: String, newName: String): Resource<Boolean>
}
