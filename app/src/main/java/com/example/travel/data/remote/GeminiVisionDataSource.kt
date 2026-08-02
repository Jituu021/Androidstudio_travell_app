package com.example.travel.data.remote

import com.example.travel.domain.model.AIPhotoAnalysis
import com.example.travel.domain.model.PhotoTag
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiVisionDataSource @Inject constructor() {

    suspend fun analyzePhoto(photoId: String, localPath: String): Pair<AIPhotoAnalysis, List<PhotoTag>> {
        val analysis = AIPhotoAnalysis(
            photoId = photoId,
            caption = "Sunset view over the historic city square",
            description = "A warm, golden-hour photograph capturing classical architecture and scenic skyline views.",
            altText = "Golden hour skyline view with historic buildings",
            detectedCategory = "Landmark",
            confidenceScore = 0.96
        )

        val tags = listOf(
            PhotoTag(id = "tag_${photoId}_1", photoId = photoId, tagName = "Landmark", confidence = 0.98),
            PhotoTag(id = "tag_${photoId}_2", photoId = photoId, tagName = "Sunset", confidence = 0.95),
            PhotoTag(id = "tag_${photoId}_3", photoId = photoId, tagName = "Architecture", confidence = 0.92)
        )

        return Pair(analysis, tags)
    }
}
