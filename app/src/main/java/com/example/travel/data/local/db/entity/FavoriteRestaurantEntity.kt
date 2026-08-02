package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_restaurants")
data class FavoriteRestaurantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val category: String,
    val cuisine: String,
    val lat: Double,
    val lon: Double,
    val address: String,
    val isVegetarian: Boolean = false,
    val isVegan: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
