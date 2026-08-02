package com.example.travel.domain.usecase.timeline

import com.example.travel.domain.model.MemoryRoute
import com.example.travel.domain.repository.MemoryMapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMemoryMapUseCase @Inject constructor(
    private val memoryMapRepository: MemoryMapRepository
) {
    operator fun invoke(tripId: String): Flow<MemoryRoute?> {
        return memoryMapRepository.getMemoryRoute(tripId)
    }
}
