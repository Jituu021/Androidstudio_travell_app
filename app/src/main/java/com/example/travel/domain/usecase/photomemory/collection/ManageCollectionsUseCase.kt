package com.example.travel.domain.usecase.photomemory.collection

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.MemoryCollection
import com.example.travel.domain.repository.MemoryCollectionRepository
import javax.inject.Inject

class ManageCollectionsUseCase @Inject constructor(
    private val memoryCollectionRepository: MemoryCollectionRepository
) {
    suspend operator fun invoke(collection: MemoryCollection): Resource<Boolean> {
        return memoryCollectionRepository.createCollection(collection)
    }
}
