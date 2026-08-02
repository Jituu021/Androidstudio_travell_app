package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.StoryChapter
import com.example.travel.domain.model.TravelStory
import kotlinx.coroutines.flow.Flow
import java.io.File

interface TravelStoryRepository {
    fun getStoryForTrip(tripId: String): Flow<TravelStory?>
    fun getChaptersForStory(storyId: String): Flow<List<StoryChapter>>
    suspend fun generateStory(tripId: String, title: String): Resource<TravelStory>
    suspend fun exportStoryAsPdf(story: TravelStory, chapters: List<StoryChapter>): Resource<File>
}
