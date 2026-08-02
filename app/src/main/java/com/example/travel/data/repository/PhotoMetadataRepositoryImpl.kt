package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.PhotoMemoryDao
import com.example.travel.data.local.db.entity.PhotoMetadataEntity
import com.example.travel.domain.model.PhotoMetadata
import com.example.travel.domain.repository.PhotoMetadataRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoMetadataRepositoryImpl @Inject constructor(
    private val photoMemoryDao: PhotoMemoryDao
) : PhotoMetadataRepository {

    override suspend fun getMetadata(photoId: String): Resource<PhotoMetadata> {
        return try {
            val entity = photoMemoryDao.getMetadataForPhoto(photoId)
            val model = if (entity != null) {
                PhotoMetadata(
                    photoId = entity.photoId,
                    cameraMake = entity.cameraMake,
                    cameraModel = entity.cameraModel,
                    iso = entity.iso,
                    fNumber = entity.fNumber,
                    exposureTime = entity.exposureTime
                )
            } else {
                PhotoMetadata(photoId = photoId)
            }
            Resource.Success(model)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to get metadata", e)
        }
    }

    override suspend fun saveMetadata(metadata: PhotoMetadata): Resource<Boolean> {
        return try {
            val entity = PhotoMetadataEntity(
                photoId = metadata.photoId,
                cameraMake = metadata.cameraMake,
                cameraModel = metadata.cameraModel,
                iso = metadata.iso,
                fNumber = metadata.fNumber,
                exposureTime = metadata.exposureTime
            )
            photoMemoryDao.insertMetadata(entity)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save metadata", e)
        }
    }
}
