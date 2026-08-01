package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.RouteCacheEntity

@Dao
interface RouteDao {
    @Query("SELECT * FROM route_cache WHERE id = :id")
    suspend fun getCachedRoute(id: String): RouteCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRouteCache(route: RouteCacheEntity)

    @Query("DELETE FROM route_cache")
    suspend fun clearRouteCache()
}
