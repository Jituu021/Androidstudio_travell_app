package com.example.travel.domain.usecase.timeline

import com.example.travel.domain.model.TimelineEvent
import com.example.travel.domain.repository.MemoryTimelineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FilterTimelineUseCase @Inject constructor(
    private val memoryTimelineRepository: MemoryTimelineRepository
) {
    operator fun invoke(tripId: String, eventType: String?): Flow<List<TimelineEvent>> {
        return memoryTimelineRepository.getTimelineEvents(tripId).map { events ->
            if (eventType.isNullOrBlank() || eventType == "ALL") events
            else events.filter { it.eventType.equals(eventType, ignoreCase = true) }
        }
    }
}
