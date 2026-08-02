package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PhotoMetadata

interface PhotoMetadataRepository {
    suspend fun getMetadata(photoId: String): Resource<PhotoMetadata>
    suspend fun saveMetadata(metadata: PhotoMetadata): Resource<Boolean>
}
