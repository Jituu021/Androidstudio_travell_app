package com.example.travel.domain.usecase.photomemory.ai

import com.example.travel.core.common.result.Resource
import javax.inject.Inject

class GeneratePhotoCaptionUseCase @Inject constructor() {
    operator fun invoke(locationName: String, category: String): Resource<String> {
        return Resource.Success("Exploring $locationName with stunning $category views ✨")
    }
}
