package com.example.travel.domain.usecase.photomemory.ai

import com.example.travel.domain.model.PhotoMemory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchPhotoByTagUseCase @Inject constructor(
    private val photoMemoryRepository: com.example.travel.domain.repository.PhotoMemoryRepository
) {
    operator fun invoke(tag: String): Flow<List<PhotoMemory>> {
        return photoMemoryRepository.getAllPhotos().map { list ->
            if (tag.isBlank()) list else list.filter { it.locationName.contains(tag, ignoreCase = true) }
        }
    }
}
