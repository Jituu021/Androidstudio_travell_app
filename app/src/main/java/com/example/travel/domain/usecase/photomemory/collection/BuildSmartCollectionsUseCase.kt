package com.example.travel.domain.usecase.photomemory.collection

import com.example.travel.domain.model.MemoryCollection
import com.example.travel.domain.repository.MemoryCollectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BuildSmartCollectionsUseCase @Inject constructor(
    private val memoryCollectionRepository: MemoryCollectionRepository
) {
    operator fun invoke(): Flow<List<MemoryCollection>> {
        return memoryCollectionRepository.getAllCollections()
    }
}
