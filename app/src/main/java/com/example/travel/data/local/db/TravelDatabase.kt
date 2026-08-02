package com.example.travel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travel.data.local.db.dao.AiTripPlanDao
import com.example.travel.data.local.db.dao.BudgetDao
import com.example.travel.data.local.db.dao.ExpenseDao
import com.example.travel.data.local.db.dao.NearbyDao
import com.example.travel.data.local.db.dao.OfflineRegionDao
import com.example.travel.data.local.db.dao.RouteDao
import com.example.travel.data.local.db.dao.SearchDao
import com.example.travel.data.local.db.dao.TripNoteDao
import com.example.travel.data.local.db.dao.UserDao
import com.example.travel.data.local.db.dao.WeatherDao
import com.example.travel.data.local.db.entity.AiTripPlanEntity
import com.example.travel.data.local.db.entity.ExpenseEntity
import com.example.travel.data.local.db.entity.FavoritePlaceEntity
import com.example.travel.data.local.db.entity.NearbyPlaceCacheEntity
import com.example.travel.data.local.db.entity.OfflineRegionEntity
import com.example.travel.data.local.db.entity.RouteCacheEntity
import com.example.travel.data.local.db.entity.SearchCacheEntity
import com.example.travel.data.local.db.entity.SearchHistoryEntity
import com.example.travel.data.local.db.entity.TripBudgetEntity
import com.example.travel.data.local.db.entity.TripNoteEntity
import com.example.travel.data.local.db.entity.UserEntity
import com.example.travel.data.local.db.entity.WeatherCacheEntity

@Database(
    entities = [
        UserEntity::class,
        TripNoteEntity::class,
        ExpenseEntity::class,
        SearchHistoryEntity::class,
        FavoritePlaceEntity::class,
        SearchCacheEntity::class,
        NearbyPlaceCacheEntity::class,
        RouteCacheEntity::class,
        OfflineRegionEntity::class,
        WeatherCacheEntity::class,
        AiTripPlanEntity::class,
        TripBudgetEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class TravelDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripNoteDao(): TripNoteDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun searchDao(): SearchDao
    abstract fun nearbyDao(): NearbyDao
    abstract fun routeDao(): RouteDao
    abstract fun offlineRegionDao(): OfflineRegionDao
    abstract fun weatherDao(): WeatherDao
    abstract fun aiTripPlanDao(): AiTripPlanDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        const val DATABASE_NAME = "travel_buddy_db"
    }
}
