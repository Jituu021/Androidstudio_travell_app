package com.example.travel.domain.usecase.photomemory.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PhotoMoment
import com.example.travel.domain.repository.AIPhotoRepository
import javax.inject.Inject

class GroupPhotoMomentsUseCase @Inject constructor(
    private val aiPhotoRepository: AIPhotoRepository
) {
    suspend operator fun invoke(tripId: String, moment: PhotoMoment): Resource<Boolean> {
        return aiPhotoRepository.groupMoments(tripId, moment)
    }
}
