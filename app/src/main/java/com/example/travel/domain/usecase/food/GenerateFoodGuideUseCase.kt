package com.example.travel.domain.usecase.food

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.FoodGuide
import com.example.travel.domain.repository.FoodRepository
import javax.inject.Inject

class GenerateFoodGuideUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(destination: String): Resource<FoodGuide> {
        return foodRepository.generateFoodGuide(destination)
    }
}
