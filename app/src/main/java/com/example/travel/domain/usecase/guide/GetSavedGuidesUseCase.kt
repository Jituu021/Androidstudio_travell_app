package com.example.travel.domain.usecase.guide

import com.example.travel.domain.model.LocalGuide
import com.example.travel.domain.repository.LocalGuideRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSavedGuidesUseCase @Inject constructor(
    private val localGuideRepository: LocalGuideRepository
) {
    operator fun invoke(): Flow<List<LocalGuide>> {
        return localGuideRepository.getAllSavedGuides()
    }
}
