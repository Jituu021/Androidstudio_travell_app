package com.example.travel.domain.usecase.photomemory.collection

import com.example.travel.domain.model.TripHighlight
import com.example.travel.domain.repository.MemoryCollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GenerateTripHighlightsUseCase @Inject constructor(
    private val memoryCollectionRepository: MemoryCollectionRepository
) {
    operator fun invoke(tripId: String): Flow<TripHighlight?> {
        return memoryCollectionRepository.getTripHighlight(tripId)
    }
}
