package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PhotoMemory
import kotlinx.coroutines.flow.Flow

interface PhotoMemoryRepository {
    fun getPhotosForTrip(tripId: String): Flow<List<PhotoMemory>>
    fun getAllPhotos(): Flow<List<PhotoMemory>>
    suspend fun addPhoto(photo: PhotoMemory): Resource<Boolean>
    suspend fun isPhotoExists(hash: String): Boolean
}
