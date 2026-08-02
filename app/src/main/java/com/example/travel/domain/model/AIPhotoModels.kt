package com.example.travel.domain.model

data class AIPhotoAnalysis(
    val photoId: String,
    val caption: String,
    val description: String,
    val altText: String = "",
    val detectedCategory: String = "Landmark",
    val confidenceScore: Double = 0.95,
    val aiVersion: String = "Gemini-Vision-v1",
    val timestamp: Long = System.currentTimeMillis()
)

data class PhotoTag(
    val id: String = "",
    val photoId: String,
    val tagName: String,
    val confidence: Double = 0.9
)

data class PhotoMoment(
    val id: String = "",
    val tripId: String,
    val title: String,
    val coverPhotoId: String,
    val photoCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis()
)
