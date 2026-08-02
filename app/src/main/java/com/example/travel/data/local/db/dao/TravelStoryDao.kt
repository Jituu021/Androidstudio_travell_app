package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.SlideshowProjectEntity
import com.example.travel.data.local.db.entity.StoryChapterEntity
import com.example.travel.data.local.db.entity.TravelStoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelStoryDao {
    @Query("SELECT * FROM travel_stories WHERE tripId = :tripId LIMIT 1")
    fun getStoryForTrip(tripId: String): Flow<TravelStoryEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTravelStory(story: TravelStoryEntity)

    @Query("SELECT * FROM story_chapters WHERE storyId = :storyId ORDER BY chapterOrder ASC")
    fun getChaptersForStory(storyId: String): Flow<List<StoryChapterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapters(chapters: List<StoryChapterEntity>)

    @Query("SELECT * FROM slideshow_projects WHERE tripId = :tripId LIMIT 1")
    fun getSlideshowForTrip(tripId: String): Flow<SlideshowProjectEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSlideshow(slideshow: SlideshowProjectEntity)
}
