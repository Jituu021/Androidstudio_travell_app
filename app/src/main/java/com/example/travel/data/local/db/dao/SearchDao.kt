package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.FavoritePlaceEntity
import com.example.travel.data.local.db.entity.SearchCacheEntity
import com.example.travel.data.local.db.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    // Search History
    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 15")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(item: SearchHistoryEntity)

    @Query("DELETE FROM search_history")
    suspend fun clearSearchHistory()

    @Query("DELETE FROM search_history WHERE id = :id")
    suspend fun deleteSearchHistoryItem(id: Long)

    // Favorite Places
    @Query("SELECT * FROM favorite_places ORDER BY timestamp DESC")
    fun getFavoritePlaces(): Flow<List<FavoritePlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoritePlace(place: FavoritePlaceEntity)

    @Query("DELETE FROM favorite_places WHERE id = :id")
    suspend fun deleteFavoritePlace(id: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_places WHERE id = :id)")
    suspend fun isFavoritePlace(id: String): Boolean

    // Search Cache
    @Query("SELECT * FROM search_cache WHERE queryKey = :query")
    suspend fun getCachedSearchResults(query: String): List<SearchCacheEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchCache(cacheItems: List<SearchCacheEntity>)
}
