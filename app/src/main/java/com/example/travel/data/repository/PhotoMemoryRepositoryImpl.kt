package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.PhotoMemoryDao
import com.example.travel.data.local.db.entity.PhotoMemoryEntity
import com.example.travel.domain.model.PhotoMemory
import com.example.travel.domain.repository.PhotoMemoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoMemoryRepositoryImpl @Inject constructor(
    private val photoMemoryDao: PhotoMemoryDao
) : PhotoMemoryRepository {

    override fun getPhotosForTrip(tripId: String): Flow<List<PhotoMemory>> {
        return photoMemoryDao.getPhotosForTrip(tripId).map { list ->
            list.map { e -> parseEntityToModel(e) }
        }
    }

    override fun getAllPhotos(): Flow<List<PhotoMemory>> {
        return photoMemoryDao.getAllPhotos().map { list ->
            list.map { e -> parseEntityToModel(e) }
        }
    }

    override suspend fun addPhoto(photo: PhotoMemory): Resource<Boolean> {
        return try {
            if (photoMemoryDao.isPhotoExists(photo.fileHash)) {
                Timber.d("Photo with hash ${photo.fileHash} already exists, skipping duplicate.")
                return Resource.Success(false)
            }
            val entity = PhotoMemoryEntity(
                id = photo.id.ifEmpty { "photo_${System.currentTimeMillis()}" },
                tripId = photo.tripId,
                localFilePath = photo.localFilePath,
                fileHash = photo.fileHash,
                captureTimestamp = photo.captureTimestamp,
                latitude = photo.latitude,
                longitude = photo.longitude,
                locationName = photo.locationName,
                orientation = photo.orientation,
                fileSizeBytes = photo.fileSizeBytes,
                width = photo.width,
                height = photo.height
            )
            photoMemoryDao.insertPhoto(entity)
            Timber.d("Added photo memory: ${photo.localFilePath}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to add photo memory", e)
        }
    }

    override suspend fun isPhotoExists(hash: String): Boolean {
        return photoMemoryDao.isPhotoExists(hash)
    }

    private fun parseEntityToModel(e: PhotoMemoryEntity): PhotoMemory {
        return PhotoMemory(
            id = e.id,
            tripId = e.tripId,
            localFilePath = e.localFilePath,
            fileHash = e.fileHash,
            captureTimestamp = e.captureTimestamp,
            latitude = e.latitude,
            longitude = e.longitude,
            locationName = e.locationName,
            orientation = e.orientation,
            fileSizeBytes = e.fileSizeBytes,
            width = e.width,
            height = e.height
        )
    }
}
