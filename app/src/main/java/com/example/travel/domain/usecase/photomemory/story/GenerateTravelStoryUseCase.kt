package com.example.travel.domain.usecase.photomemory.story

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.TravelStory
import com.example.travel.domain.repository.TravelStoryRepository
import javax.inject.Inject

class GenerateTravelStoryUseCase @Inject constructor(
    private val travelStoryRepository: TravelStoryRepository
) {
    suspend operator fun invoke(tripId: String, title: String): Resource<TravelStory> {
        return travelStoryRepository.generateStory(tripId, title)
    }
}
