package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_photo_analyses")
data class AIPhotoAnalysisEntity(
    @PrimaryKey val photoId: String,
    val caption: String,
    val description: String,
    val altText: String = "",
    val detectedCategory: String = "Landmark",
    val confidenceScore: Double = 0.95,
    val aiVersion: String = "Gemini-Vision-v1",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "photo_tags")
data class PhotoTagEntity(
    @PrimaryKey val id: String,
    val photoId: String,
    val tagName: String,
    val confidence: Double = 0.9
)

@Entity(tableName = "photo_moments")
data class PhotoMomentEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val title: String,
    val coverPhotoId: String,
    val photoCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)
