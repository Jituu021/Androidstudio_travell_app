package com.example.travel.domain.usecase.photomemory.story

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SlideshowProject
import com.example.travel.domain.repository.SlideshowRepository
import javax.inject.Inject

class CreateSlideshowUseCase @Inject constructor(
    private val slideshowRepository: SlideshowRepository
) {
    suspend operator fun invoke(project: SlideshowProject): Resource<Boolean> {
        return slideshowRepository.createSlideshow(project)
    }
}
