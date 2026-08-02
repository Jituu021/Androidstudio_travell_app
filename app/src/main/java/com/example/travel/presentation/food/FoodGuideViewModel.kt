package com.example.travel.presentation.food

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.FoodGuide
import com.example.travel.domain.model.Restaurant
import com.example.travel.domain.repository.FoodRepository
import com.example.travel.domain.usecase.food.GenerateFoodGuideUseCase
import com.example.travel.domain.usecase.food.SaveFavoriteRestaurantUseCase
import com.example.travel.domain.usecase.food.SearchRestaurantsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FoodUiState(
    val restaurants: List<Restaurant> = emptyList(),
    val favoriteRestaurants: List<Restaurant> = emptyList(),
    val foodGuide: FoodGuide? = null,
    val isLoading: Boolean = false,
    val selectedCategory: String = "All",
    val isVegOnly: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class FoodGuideViewModel @Inject constructor(
    private val foodRepository: FoodRepository,
    private val searchRestaurantsUseCase: SearchRestaurantsUseCase,
    private val generateFoodGuideUseCase: GenerateFoodGuideUseCase,
    private val saveFavoriteRestaurantUseCase: SaveFavoriteRestaurantUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FoodUiState())
    val uiState: StateFlow<FoodUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
        searchFoodPlaces(34.0522, -118.2437, "All", false)
        generateGuideForDestination("Tokyo")
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            foodRepository.getFavoriteRestaurants().collect { list ->
                _uiState.value = _uiState.value.copy(favoriteRestaurants = list)
            }
        }
    }

    fun searchFoodPlaces(lat: Double, lon: Double, category: String, isVegOnly: Boolean) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, selectedCategory = category, isVegOnly = isVegOnly)
            when (val result = searchRestaurantsUseCase(lat, lon, category, isVegOnly)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, restaurants = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun generateGuideForDestination(destination: String) {
        viewModelScope.launch {
            when (val result = generateFoodGuideUseCase(destination)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(foodGuide = result.data)
                }
                else -> {}
            }
        }
    }

    fun toggleFavorite(restaurant: Restaurant) {
        viewModelScope.launch {
            if (restaurant.isFavorite) {
                foodRepository.deleteFavoriteRestaurant(restaurant.id)
            } else {
                saveFavoriteRestaurantUseCase(restaurant)
            }
        }
    }
}
