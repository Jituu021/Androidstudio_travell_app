package com.example.travel.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.travel.data.local.db.dao.AiTripPlanDao
import com.example.travel.data.local.db.dao.BudgetDao
import com.example.travel.data.local.db.dao.ExpenseDao
import com.example.travel.data.local.db.dao.FoodDao
import com.example.travel.data.local.db.dao.GroupExpenseDao
import com.example.travel.data.local.db.dao.GroupTripDao
import com.example.travel.data.local.db.dao.JournalDao
import com.example.travel.data.local.db.dao.LocalGuideDao
import com.example.travel.data.local.db.dao.NearbyDao
import com.example.travel.data.local.db.dao.OfflineRegionDao
import com.example.travel.data.local.db.dao.PackingDao
import com.example.travel.data.local.db.dao.PhotoMemoryDao
import com.example.travel.data.local.db.dao.RouteDao
import com.example.travel.data.local.db.dao.SafetyDao
import com.example.travel.data.local.db.dao.SearchDao
import com.example.travel.data.local.db.dao.TimelineDao
import com.example.travel.data.local.db.dao.TripNoteDao
import com.example.travel.data.local.db.dao.UserDao
import com.example.travel.data.local.db.dao.WeatherDao
import com.example.travel.data.local.db.entity.AIJournalSummaryEntity
import com.example.travel.data.local.db.entity.AIPhotoAnalysisEntity
import com.example.travel.data.local.db.entity.AITripStoryEntity
import com.example.travel.data.local.db.entity.AiTripPlanEntity
import com.example.travel.data.local.db.entity.ExpenseEntity
import com.example.travel.data.local.db.entity.FavoritePlaceEntity
import com.example.travel.data.local.db.entity.FavoriteRestaurantEntity
import com.example.travel.data.local.db.entity.GroupExpenseEntity
import com.example.travel.data.local.db.entity.GroupTripEntity
import com.example.travel.data.local.db.entity.JournalEntity
import com.example.travel.data.local.db.entity.JournalEntryEntity
import com.example.travel.data.local.db.entity.JournalLocationEntity
import com.example.travel.data.local.db.entity.JournalMediaEntity
import com.example.travel.data.local.db.entity.LocalGuideEntity
import com.example.travel.data.local.db.entity.MemoryRouteEntity
import com.example.travel.data.local.db.entity.NearbyPlaceCacheEntity
import com.example.travel.data.local.db.entity.OfflineRegionEntity
import com.example.travel.data.local.db.entity.PackingItemEntity
import com.example.travel.data.local.db.entity.PhotoMemoryEntity
import com.example.travel.data.local.db.entity.PhotoMetadataEntity
import com.example.travel.data.local.db.entity.PhotoMomentEntity
import com.example.travel.data.local.db.entity.PhotoTagEntity
import com.example.travel.data.local.db.entity.RouteCacheEntity
import com.example.travel.data.local.db.entity.SafetyReportEntity
import com.example.travel.data.local.db.entity.SearchCacheEntity
import com.example.travel.data.local.db.entity.SearchHistoryEntity
import com.example.travel.data.local.db.entity.SettlementEntity
import com.example.travel.data.local.db.entity.TimelineEventEntity
import com.example.travel.data.local.db.entity.TripBudgetEntity
import com.example.travel.data.local.db.entity.TripMemberEntity
import com.example.travel.data.local.db.entity.TripNoteEntity
import com.example.travel.data.local.db.entity.TripStatisticsEntity
import com.example.travel.data.local.db.entity.UserEntity
import com.example.travel.data.local.db.entity.VoiceNoteEntity
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
        TripBudgetEntity::class,
        PackingItemEntity::class,
        SafetyReportEntity::class,
        LocalGuideEntity::class,
        GroupTripEntity::class,
        TripMemberEntity::class,
        GroupExpenseEntity::class,
        SettlementEntity::class,
        FavoriteRestaurantEntity::class,
        JournalEntity::class,
        JournalEntryEntity::class,
        JournalMediaEntity::class,
        JournalLocationEntity::class,
        VoiceNoteEntity::class,
        AIJournalSummaryEntity::class,
        AITripStoryEntity::class,
        PhotoMemoryEntity::class,
        PhotoMetadataEntity::class,
        AIPhotoAnalysisEntity::class,
        PhotoTagEntity::class,
        PhotoMomentEntity::class,
        TimelineEventEntity::class,
        MemoryRouteEntity::class,
        TripStatisticsEntity::class
    ],
    version = 21,
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
    abstract fun packingDao(): PackingDao
    abstract fun safetyDao(): SafetyDao
    abstract fun localGuideDao(): LocalGuideDao
    abstract fun groupTripDao(): GroupTripDao
    abstract fun groupExpenseDao(): GroupExpenseDao
    abstract fun foodDao(): FoodDao
    abstract fun journalDao(): JournalDao
    abstract fun photoMemoryDao(): PhotoMemoryDao
    abstract fun timelineDao(): TimelineDao

    companion object {
        const val DATABASE_NAME = "travel_buddy_db"
    }
}
