package com.example.travel.domain.usecase.photomemory.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AIPhotoAnalysis
import com.example.travel.domain.repository.AIPhotoRepository
import javax.inject.Inject

class AnalyzePhotoUseCase @Inject constructor(
    private val aiPhotoRepository: AIPhotoRepository
) {
    suspend operator fun invoke(photoId: String, localPath: String): Resource<AIPhotoAnalysis> {
        return aiPhotoRepository.analyzePhoto(photoId, localPath)
    }
}
