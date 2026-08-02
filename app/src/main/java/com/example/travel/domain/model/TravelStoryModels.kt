package com.example.travel.domain.model

data class TravelStory(
    val id: String = "",
    val tripId: String,
    val title: String,
    val coverPhotoUrl: String = "",
    val theme: String = "Classic",
    val fontStyle: String = "Default",
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)

data class StoryChapter(
    val id: String = "",
    val storyId: String,
    val chapterTitle: String,
    val chapterSummary: String,
    val bestMoments: List<String> = emptyList(),
    val chapterOrder: Int = 0
)

data class SlideshowProject(
    val id: String = "",
    val tripId: String,
    val title: String,
    val selectedPhotoIds: List<String> = emptyList(),
    val transitionSpeedSeconds: Int = 3,
    val backgroundMusicFile: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
