package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel_stories")
data class TravelStoryEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val coverPhotoUrl: String = "",
    val theme: String = "Classic",
    val fontStyle: String = "Default",
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "story_chapters")
data class StoryChapterEntity(
    @PrimaryKey val id: String,
    val storyId: String,
    val chapterTitle: String,
    val chapterSummary: String,
    val bestMomentsJson: String = "[]",
    val chapterOrder: Int = 0
)

@Entity(tableName = "slideshow_projects")
data class SlideshowProjectEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val selectedPhotoIdsJson: String = "[]",
    val transitionSpeedSeconds: Int = 3,
    val backgroundMusicFile: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
