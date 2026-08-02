package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AIPhotoAnalysis
import com.example.travel.domain.model.PhotoMoment
import com.example.travel.domain.model.PhotoTag
import kotlinx.coroutines.flow.Flow

interface AIPhotoRepository {
    fun getAnalysis(photoId: String): Flow<AIPhotoAnalysis?>
    fun getTags(photoId: String): Flow<List<PhotoTag>>
    fun getMoments(tripId: String): Flow<List<PhotoMoment>>
    suspend fun analyzePhoto(photoId: String, localPath: String): Resource<AIPhotoAnalysis>
    suspend fun groupMoments(tripId: String, moment: PhotoMoment): Resource<Boolean>
}
