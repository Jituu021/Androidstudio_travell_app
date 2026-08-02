package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.JournalDao
import com.example.travel.data.local.db.entity.JournalLocationEntity
import com.example.travel.data.local.db.entity.JournalMediaEntity
import com.example.travel.domain.model.JournalLocation
import com.example.travel.domain.model.JournalMedia
import com.example.travel.domain.repository.JournalMediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalMediaRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : JournalMediaRepository {

    override fun getMediaForEntry(entryId: String): Flow<List<JournalMedia>> {
        return journalDao.getMediaForEntry(entryId).map { list ->
            list.map { e ->
                JournalMedia(
                    id = e.id,
                    entryId = e.entryId,
                    mediaType = e.mediaType,
                    localFilePath = e.localFilePath,
                    mimeType = e.mimeType,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getLocationForEntry(entryId: String): Flow<JournalLocation?> {
        return journalDao.getLocationForEntry(entryId).map { entity ->
            entity?.let { e ->
                JournalLocation(
                    id = e.id,
                    entryId = e.entryId,
                    locationName = e.locationName,
                    latitude = e.latitude,
                    longitude = e.longitude,
                    address = e.address,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun attachMedia(media: JournalMedia): Resource<Boolean> {
        return try {
            val entity = JournalMediaEntity(
                id = media.id.ifEmpty { "media_${System.currentTimeMillis()}" },
                entryId = media.entryId,
                mediaType = media.mediaType,
                localFilePath = media.localFilePath,
                mimeType = media.mimeType
            )
            journalDao.insertMedia(entity)
            Timber.d("Attached media (${media.mediaType}): ${media.localFilePath}")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error attaching media")
            Resource.Error(e.message ?: "Failed to attach media", e)
        }
    }

    override suspend fun removeMedia(id: String): Resource<Boolean> {
        return try {
            journalDao.deleteMedia(id)
            Timber.d("Removed media: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to remove media", e)
        }
    }

    override suspend fun setLocation(location: JournalLocation): Resource<Boolean> {
        return try {
            val entity = JournalLocationEntity(
                id = location.id.ifEmpty { "loc_${System.currentTimeMillis()}" },
                entryId = location.entryId,
                locationName = location.locationName,
                latitude = location.latitude,
                longitude = location.longitude,
                address = location.address
            )
            journalDao.insertLocation(entity)
            Timber.d("Set journal location: ${location.locationName}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to set location", e)
        }
    }
}
