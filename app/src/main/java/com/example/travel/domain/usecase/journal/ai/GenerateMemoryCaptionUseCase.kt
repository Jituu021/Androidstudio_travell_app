package com.example.travel.domain.usecase.journal.ai

import com.example.travel.core.common.result.Resource
import javax.inject.Inject

class GenerateMemoryCaptionUseCase @Inject constructor() {
    operator fun invoke(locationName: String, mood: String): Resource<String> {
        return Resource.Success("A magical moment in $locationName feeling $mood ✨")
    }
}
