package com.example.travel.domain.usecase.food

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Restaurant
import com.example.travel.domain.repository.FoodRepository
import javax.inject.Inject

class SearchRestaurantsUseCase @Inject constructor(
    private val foodRepository: FoodRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double, category: String, isVegOnly: Boolean): Resource<List<Restaurant>> {
        return foodRepository.searchRestaurants(lat, lon, category, isVegOnly)
    }
}
