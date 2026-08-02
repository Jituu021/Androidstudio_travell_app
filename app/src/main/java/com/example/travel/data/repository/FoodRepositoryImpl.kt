package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.FoodDao
import com.example.travel.data.local.db.entity.FavoriteRestaurantEntity
import com.example.travel.domain.model.FoodGuide
import com.example.travel.domain.model.Restaurant
import com.example.travel.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FoodRepositoryImpl @Inject constructor(
    private val foodDao: FoodDao
) : FoodRepository {

    override suspend fun searchRestaurants(
        lat: Double,
        lon: Double,
        category: String,
        isVegOnly: Boolean
    ): Resource<List<Restaurant>> {
        return try {
            val list = listOf(
                Restaurant(
                    id = "rest_1",
                    name = "The Spice Route Bistro",
                    category = "Restaurant",
                    cuisine = "Authentic Local & North Indian",
                    distanceKm = 0.6,
                    travelTimeMinutes = 8,
                    lat = lat + 0.003,
                    lon = lon + 0.003,
                    address = "12 Main Boulevard, City Center",
                    isVegetarian = true,
                    isVegan = false
                ),
                Restaurant(
                    id = "rest_2",
                    name = "Artisan Organic Cafe",
                    category = "Cafe",
                    cuisine = "Continental & Espresso",
                    distanceKm = 1.1,
                    travelTimeMinutes = 14,
                    lat = lat - 0.004,
                    lon = lon + 0.002,
                    address = "45 Heritage Square",
                    isVegetarian = true,
                    isVegan = true
                ),
                Restaurant(
                    id = "rest_3",
                    name = "Golden Dragon Noodles",
                    category = "Street Food",
                    cuisine = "Pan-Asian & Street Food",
                    distanceKm = 1.8,
                    travelTimeMinutes = 20,
                    lat = lat + 0.005,
                    lon = lon - 0.005,
                    address = "88 Riverside Promenade",
                    isVegetarian = false
                )
            )

            val filtered = if (isVegOnly) list.filter { it.isVegetarian } else list
            Timber.d("Found ${filtered.size} nearby food places")
            Resource.Success(filtered)
        } catch (e: Exception) {
            Timber.e(e, "Error searching restaurants")
            Resource.Error(e.message ?: "Failed to search restaurants", e)
        }
    }

    override suspend fun generateFoodGuide(destination: String): Resource<FoodGuide> {
        return try {
            val guide = FoodGuide(
                destination = destination,
                signatureDishes = listOf("Classic Regional Thali", "Wood-fired Artisan Pizza", "Traditional Honey Tea"),
                diningEtiquette = "Tipping of 10% is customary at sit-down restaurants. Street food vendors accept QR/Cash payments.",
                recommendedMealTimes = "Lunch: 12:30 PM - 02:30 PM | Dinner: 07:30 PM - 10:00 PM",
                mustTrySpecialties = listOf("Local Claypot Curry", "Handcrafted Pistachio Kulfi")
            )
            Resource.Success(guide)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to generate food guide", e)
        }
    }

    override fun getFavoriteRestaurants(): Flow<List<Restaurant>> {
        return foodDao.getAllFavoriteRestaurants().map { list ->
            list.map { e ->
                Restaurant(
                    id = e.id,
                    name = e.name,
                    category = e.category,
                    cuisine = e.cuisine,
                    lat = e.lat,
                    lon = e.lon,
                    address = e.address,
                    isVegetarian = e.isVegetarian,
                    isVegan = e.isVegan,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun saveFavoriteRestaurant(restaurant: Restaurant): Resource<Boolean> {
        return try {
            val entity = FavoriteRestaurantEntity(
                id = restaurant.id,
                name = restaurant.name,
                category = restaurant.category,
                cuisine = restaurant.cuisine,
                lat = restaurant.lat,
                lon = restaurant.lon,
                address = restaurant.address,
                isVegetarian = restaurant.isVegetarian,
                isVegan = restaurant.isVegan
            )
            foodDao.insertFavoriteRestaurant(entity)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save favorite restaurant", e)
        }
    }

    override suspend fun deleteFavoriteRestaurant(id: String): Resource<Boolean> {
        return try {
            foodDao.deleteFavoriteRestaurant(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete favorite restaurant", e)
        }
    }
}
