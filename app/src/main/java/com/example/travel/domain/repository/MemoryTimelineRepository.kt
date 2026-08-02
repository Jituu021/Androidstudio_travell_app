package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.TimelineEvent
import com.example.travel.domain.model.TripStatistics
import kotlinx.coroutines.flow.Flow

interface MemoryTimelineRepository {
    fun getTimelineEvents(tripId: String): Flow<List<TimelineEvent>>
    fun getTripStatistics(tripId: String): Flow<TripStatistics?>
    suspend fun saveTimelineEvents(tripId: String, events: List<TimelineEvent>): Resource<Boolean>
    suspend fun updateTripStatistics(stats: TripStatistics): Resource<Boolean>
}
