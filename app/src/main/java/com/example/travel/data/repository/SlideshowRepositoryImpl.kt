package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.TravelStoryDao
import com.example.travel.data.local.db.entity.SlideshowProjectEntity
import com.example.travel.domain.model.SlideshowProject
import com.example.travel.domain.repository.SlideshowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SlideshowRepositoryImpl @Inject constructor(
    private val travelStoryDao: TravelStoryDao
) : SlideshowRepository {

    override fun getSlideshowForTrip(tripId: String): Flow<SlideshowProject?> {
        return travelStoryDao.getSlideshowForTrip(tripId).map { entity ->
            entity?.let { e ->
                val photosList = mutableListOf<String>()
                try {
                    val arr = JSONArray(e.selectedPhotoIdsJson)
                    for (i in 0 until arr.length()) photosList.add(arr.getString(i))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                SlideshowProject(
                    id = e.id,
                    tripId = e.tripId,
                    title = e.title,
                    selectedPhotoIds = photosList,
                    transitionSpeedSeconds = e.transitionSpeedSeconds,
                    backgroundMusicFile = e.backgroundMusicFile,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun createSlideshow(project: SlideshowProject): Resource<Boolean> {
        return try {
            val entity = SlideshowProjectEntity(
                id = project.id.ifEmpty { "slide_${System.currentTimeMillis()}" },
                tripId = project.tripId,
                title = project.title,
                selectedPhotoIdsJson = JSONArray(project.selectedPhotoIds).toString(),
                transitionSpeedSeconds = project.transitionSpeedSeconds,
                backgroundMusicFile = project.backgroundMusicFile
            )
            travelStoryDao.insertSlideshow(entity)
            Timber.d("Created Slideshow Project: ${project.title}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create slideshow project", e)
        }
    }
}
