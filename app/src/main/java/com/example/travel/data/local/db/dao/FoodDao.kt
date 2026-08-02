package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.FavoriteRestaurantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM favorite_restaurants ORDER BY timestamp DESC")
    fun getAllFavoriteRestaurants(): Flow<List<FavoriteRestaurantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRestaurant(restaurant: FavoriteRestaurantEntity)

    @Query("DELETE FROM favorite_restaurants WHERE id = :id")
    suspend fun deleteFavoriteRestaurant(id: String)
}
