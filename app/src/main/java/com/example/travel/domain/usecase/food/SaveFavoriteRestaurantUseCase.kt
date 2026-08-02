package com.example.travel.domain.usecase.food

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Restaurant
import com.example.travel.domain.repository.FoodRepository
import javax.inject.Inject

class SaveFavoriteRestaurantUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(restaurant: Restaurant): Resource<Boolean> {
        return foodRepository.saveFavoriteRestaurant(restaurant)
    }
}
