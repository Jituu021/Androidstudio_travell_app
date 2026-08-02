package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.TimelineDao
import com.example.travel.data.local.db.entity.TimelineEventEntity
import com.example.travel.data.local.db.entity.TripStatisticsEntity
import com.example.travel.domain.model.TimelineEvent
import com.example.travel.domain.model.TripStatistics
import com.example.travel.domain.repository.MemoryTimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryTimelineRepositoryImpl @Inject constructor(
    private val timelineDao: TimelineDao
) : MemoryTimelineRepository {

    override fun getTimelineEvents(tripId: String): Flow<List<TimelineEvent>> {
        return timelineDao.getTimelineEventsForTrip(tripId).map { list ->
            list.map { e ->
                TimelineEvent(
                    id = e.id,
                    tripId = e.tripId,
                    eventType = e.eventType,
                    title = e.title,
                    description = e.description,
                    latitude = e.latitude,
                    longitude = e.longitude,
                    locationName = e.locationName,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getTripStatistics(tripId: String): Flow<TripStatistics?> {
        return timelineDao.getTripStatistics(tripId).map { entity ->
            entity?.let { e ->
                TripStatistics(
                    tripId = e.tripId,
                    totalDistanceKm = e.totalDistanceKm,
                    placesVisitedCount = e.placesVisitedCount,
                    citiesVisitedCount = e.citiesVisitedCount,
                    photosCount = e.photosCount,
                    entriesCount = e.entriesCount,
                    totalExpensesAmount = e.totalExpensesAmount,
                    restaurantsExploredCount = e.restaurantsExploredCount,
                    lastUpdated = e.lastUpdated
                )
            }
        }
    }

    override suspend fun saveTimelineEvents(
        tripId: String,
        events: List<TimelineEvent>
    ): Resource<Boolean> {
        return try {
            val entities = events.map { e ->
                TimelineEventEntity(
                    id = e.id.ifEmpty { "event_${System.currentTimeMillis()}" },
                    tripId = tripId,
                    eventType = e.eventType,
                    title = e.title,
                    description = e.description,
                    latitude = e.latitude,
                    longitude = e.longitude,
                    locationName = e.locationName,
                    timestamp = e.timestamp
                )
            }
            timelineDao.insertAllEvents(entities)
            Timber.d("Saved ${events.size} timeline events for trip $tripId")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save timeline events", e)
        }
    }

    override suspend fun updateTripStatistics(stats: TripStatistics): Resource<Boolean> {
        return try {
            val entity = TripStatisticsEntity(
                tripId = stats.tripId,
                totalDistanceKm = stats.totalDistanceKm,
                placesVisitedCount = stats.placesVisitedCount,
                citiesVisitedCount = stats.citiesVisitedCount,
                photosCount = stats.photosCount,
                entriesCount = stats.entriesCount,
                totalExpensesAmount = stats.totalExpensesAmount,
                restaurantsExploredCount = stats.restaurantsExploredCount,
                lastUpdated = System.currentTimeMillis()
            )
            timelineDao.insertTripStatistics(entity)
            Timber.d("Updated statistics for trip ${stats.tripId}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update trip statistics", e)
        }
    }
}
