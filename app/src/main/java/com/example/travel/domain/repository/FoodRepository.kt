package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.FoodGuide
import com.example.travel.domain.model.Restaurant
import kotlinx.coroutines.flow.Flow

interface FoodRepository {
    suspend fun searchRestaurants(lat: Double, lon: Double, category: String, isVegOnly: Boolean): Resource<List<Restaurant>>
    suspend fun generateFoodGuide(destination: String): Resource<FoodGuide>
    fun getFavoriteRestaurants(): Flow<List<Restaurant>>
    suspend fun saveFavoriteRestaurant(restaurant: Restaurant): Resource<Boolean>
    suspend fun deleteFavoriteRestaurant(id: String): Resource<Boolean>
}
