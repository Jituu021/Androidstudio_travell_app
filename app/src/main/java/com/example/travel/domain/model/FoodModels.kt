package com.example.travel.domain.model

data class Restaurant(
    val id: String = "",
    val name: String,
    val category: String, // "Restaurant", "Cafe", "Street Food", "Bakery"
    val cuisine: String = "Local & Multi-Cuisine",
    val distanceKm: Double = 0.5,
    val travelTimeMinutes: Int = 8,
    val lat: Double = 0.0,
    val lon: Double = 0.0,
    val address: String = "",
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val openingHours: String = "09:00 AM - 10:30 PM",
    val isFavorite: Boolean = false
)

data class FoodGuide(
    val destination: String,
    val signatureDishes: List<String> = emptyList(),
    val diningEtiquette: String = "",
    val recommendedMealTimes: String = "Lunch: 12:30 PM - 02:30 PM, Dinner: 07:30 PM - 10:00 PM",
    val mustTrySpecialties: List<String> = emptyList()
)
