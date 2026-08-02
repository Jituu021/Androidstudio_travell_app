package com.example.travel.domain.usecase.photomemory

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PhotoMetadata
import com.example.travel.domain.repository.PhotoMetadataRepository
import javax.inject.Inject

class ExtractPhotoMetadataUseCase @Inject constructor(
    private val photoMetadataRepository: PhotoMetadataRepository
) {
    suspend operator fun invoke(photoId: String): Resource<PhotoMetadata> {
        return photoMetadataRepository.getMetadata(photoId)
    }
}
