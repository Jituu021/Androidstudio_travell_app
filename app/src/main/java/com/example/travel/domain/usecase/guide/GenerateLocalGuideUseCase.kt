package com.example.travel.domain.usecase.guide

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.LocalGuide
import com.example.travel.domain.repository.LocalGuideRepository
import javax.inject.Inject

class GenerateLocalGuideUseCase @Inject constructor(
    private val localGuideRepository: LocalGuideRepository
) {
    suspend operator fun invoke(destination: String): Resource<LocalGuide> {
        return localGuideRepository.generateLocalGuide(destination)
    }
}
