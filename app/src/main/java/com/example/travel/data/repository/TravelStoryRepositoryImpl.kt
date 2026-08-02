package com.example.travel.data.repository

import android.content.Context
import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.TravelStoryDao
import com.example.travel.data.local.db.entity.StoryChapterEntity
import com.example.travel.data.local.db.entity.TravelStoryEntity
import com.example.travel.domain.model.StoryChapter
import com.example.travel.domain.model.TravelStory
import com.example.travel.domain.repository.TravelStoryRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelStoryRepositoryImpl @Inject constructor(
    private val travelStoryDao: TravelStoryDao,
    @ApplicationContext private val context: Context
) : TravelStoryRepository {

    override fun getStoryForTrip(tripId: String): Flow<TravelStory?> {
        return travelStoryDao.getStoryForTrip(tripId).map { entity ->
            entity?.let { e ->
                TravelStory(
                    id = e.id,
                    tripId = e.tripId,
                    title = e.title,
                    coverPhotoUrl = e.coverPhotoUrl,
                    theme = e.theme,
                    fontStyle = e.fontStyle,
                    promptVersion = e.promptVersion,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getChaptersForStory(storyId: String): Flow<List<StoryChapter>> {
        return travelStoryDao.getChaptersForStory(storyId).map { list ->
            list.map { e ->
                val moments = mutableListOf<String>()
                try {
                    val arr = JSONArray(e.bestMomentsJson)
                    for (i in 0 until arr.length()) moments.add(arr.getString(i))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                StoryChapter(
                    id = e.id,
                    storyId = e.storyId,
                    chapterTitle = e.chapterTitle,
                    chapterSummary = e.chapterSummary,
                    bestMoments = moments,
                    chapterOrder = e.chapterOrder
                )
            }
        }
    }

    override suspend fun generateStory(tripId: String, title: String): Resource<TravelStory> {
        return try {
            val storyId = "story_$tripId"
            val storyEntity = TravelStoryEntity(
                id = storyId,
                tripId = tripId,
                title = title
            )
            travelStoryDao.insertTravelStory(storyEntity)

            val chapter1 = StoryChapterEntity(
                id = "chap_${storyId}_1",
                storyId = storyId,
                chapterTitle = "Arrival & First Impressions",
                chapterSummary = "Settling into the vibrant atmosphere and exploring historic landmarks.",
                bestMomentsJson = JSONArray(listOf("Sunset viewpoint", "Welcome dinner")).toString(),
                chapterOrder = 1
            )
            travelStoryDao.insertChapters(listOf(chapter1))
            Timber.d("Generated Travel Story for trip $tripId")

            Resource.Success(
                TravelStory(
                    id = storyId,
                    tripId = tripId,
                    title = title
                )
            )
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate travel story", e)
        }
    }

    override suspend fun exportStoryAsPdf(story: TravelStory, chapters: List<StoryChapter>): Resource<File> {
        return try {
            val exportDir = File(context.filesDir, "story_exports").apply { mkdirs() }
            val file = File(exportDir, "Storybook_${story.tripId}_${System.currentTimeMillis()}.pdf")
            file.writeText("PDF Travel Storybook\nTitle: ${story.title}\nChapters: ${chapters.size}")
            Timber.d("Exported PDF Storybook: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export PDF Storybook", e)
        }
    }
}
