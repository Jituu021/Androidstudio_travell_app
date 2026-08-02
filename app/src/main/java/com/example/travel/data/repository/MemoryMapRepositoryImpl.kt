package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.TimelineDao
import com.example.travel.data.local.db.entity.MemoryRouteEntity
import com.example.travel.domain.model.MemoryRoute
import com.example.travel.domain.repository.MemoryMapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryMapRepositoryImpl @Inject constructor(
    private val timelineDao: TimelineDao
) : MemoryMapRepository {

    override fun getMemoryRoute(tripId: String): Flow<MemoryRoute?> {
        return timelineDao.getRouteForTrip(tripId).map { entity ->
            entity?.let { e ->
                MemoryRoute(
                    id = e.id,
                    tripId = e.tripId,
                    polylineJson = e.polylineJson,
                    totalDistanceKm = e.totalDistanceKm,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun saveMemoryRoute(route: MemoryRoute): Resource<Boolean> {
        return try {
            val entity = MemoryRouteEntity(
                id = route.id.ifEmpty { "route_${System.currentTimeMillis()}" },
                tripId = route.tripId,
                polylineJson = route.polylineJson,
                totalDistanceKm = route.totalDistanceKm
            )
            timelineDao.insertRoute(entity)
            Timber.d("Saved memory route for trip ${route.tripId}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save memory route", e)
        }
    }
}
