package com.example.travel.domain.usecase.timeline

import com.example.travel.domain.model.TripStatistics
import com.example.travel.domain.repository.MemoryTimelineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CalculateTripStatisticsUseCase @Inject constructor(
    private val memoryTimelineRepository: MemoryTimelineRepository
) {
    operator fun invoke(tripId: String): Flow<TripStatistics?> {
        return memoryTimelineRepository.getTripStatistics(tripId)
    }
}
