package com.example.travel.domain.usecase.photomemory

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PhotoMemory
import com.example.travel.domain.repository.PhotoMemoryRepository
import javax.inject.Inject

class ScanTripPhotosUseCase @Inject constructor(
    private val photoMemoryRepository: PhotoMemoryRepository
) {
    suspend operator fun invoke(photo: PhotoMemory): Resource<Boolean> {
        return photoMemoryRepository.addPhoto(photo)
    }
}
