package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.NearbyPlaceCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NearbyDao {
    @Query("SELECT * FROM nearby_places_cache WHERE category = :category ORDER BY distanceMeters ASC")
    fun getNearbyPlacesByCategory(category: String): Flow<List<NearbyPlaceCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNearbyPlaces(places: List<NearbyPlaceCacheEntity>)

    @Query("DELETE FROM nearby_places_cache WHERE category = :category")
    suspend fun clearCategoryCache(category: String)

    @Query("DELETE FROM nearby_places_cache")
    suspend fun clearAllCache()
}
