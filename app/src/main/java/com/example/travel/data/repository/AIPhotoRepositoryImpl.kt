package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.PhotoMemoryDao
import com.example.travel.data.local.db.entity.AIPhotoAnalysisEntity
import com.example.travel.data.local.db.entity.PhotoMomentEntity
import com.example.travel.data.local.db.entity.PhotoTagEntity
import com.example.travel.data.remote.GeminiVisionDataSource
import com.example.travel.domain.model.AIPhotoAnalysis
import com.example.travel.domain.model.PhotoMoment
import com.example.travel.domain.model.PhotoTag
import com.example.travel.domain.repository.AIPhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIPhotoRepositoryImpl @Inject constructor(
    private val photoMemoryDao: PhotoMemoryDao,
    private val geminiVisionDataSource: GeminiVisionDataSource
) : AIPhotoRepository {

    override fun getAnalysis(photoId: String): Flow<AIPhotoAnalysis?> {
        return photoMemoryDao.getAIAnalysis(photoId).map { entity ->
            entity?.let { e ->
                AIPhotoAnalysis(
                    photoId = e.photoId,
                    caption = e.caption,
                    description = e.description,
                    altText = e.altText,
                    detectedCategory = e.detectedCategory,
                    confidenceScore = e.confidenceScore,
                    aiVersion = e.aiVersion,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getTags(photoId: String): Flow<List<PhotoTag>> {
        return photoMemoryDao.getTagsForPhoto(photoId).map { list ->
            list.map { e ->
                PhotoTag(
                    id = e.id,
                    photoId = e.photoId,
                    tagName = e.tagName,
                    confidence = e.confidence
                )
            }
        }
    }

    override fun getMoments(tripId: String): Flow<List<PhotoMoment>> {
        return photoMemoryDao.getMomentsForTrip(tripId).map { list ->
            list.map { e ->
                PhotoMoment(
                    id = e.id,
                    tripId = e.tripId,
                    title = e.title,
                    coverPhotoId = e.coverPhotoId,
                    photoCount = e.photoCount,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun analyzePhoto(photoId: String, localPath: String): Resource<AIPhotoAnalysis> {
        return try {
            val (analysis, tags) = geminiVisionDataSource.analyzePhoto(photoId, localPath)
            val analysisEntity = AIPhotoAnalysisEntity(
                photoId = analysis.photoId,
                caption = analysis.caption,
                description = analysis.description,
                altText = analysis.altText,
                detectedCategory = analysis.detectedCategory,
                confidenceScore = analysis.confidenceScore,
                aiVersion = analysis.aiVersion
            )
            photoMemoryDao.insertAIAnalysis(analysisEntity)

            val tagEntities = tags.map { t ->
                PhotoTagEntity(
                    id = t.id,
                    photoId = t.photoId,
                    tagName = t.tagName,
                    confidence = t.confidence
                )
            }
            photoMemoryDao.insertTags(tagEntities)
            Timber.d("Analyzed photo $photoId with Gemini Vision")
            Resource.Success(analysis)
        } catch (e: Exception) {
            Timber.e(e, "Error analyzing photo $photoId")
            Resource.Error(e.message ?: "Failed to analyze photo", e)
        }
    }

    override suspend fun groupMoments(tripId: String, moment: PhotoMoment): Resource<Boolean> {
        return try {
            val entity = PhotoMomentEntity(
                id = moment.id.ifEmpty { "moment_${System.currentTimeMillis()}" },
                tripId = tripId,
                title = moment.title,
                coverPhotoId = moment.coverPhotoId,
                photoCount = moment.photoCount
            )
            photoMemoryDao.insertMoment(entity)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to group moment", e)
        }
    }
}
