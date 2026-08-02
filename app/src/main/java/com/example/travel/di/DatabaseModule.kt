package com.example.travel.di

import android.content.Context
import androidx.room.Room
import com.example.travel.data.local.db.TravelDatabase
import com.example.travel.data.local.db.dao.ExpenseDao
import com.example.travel.data.local.db.dao.TripNoteDao
import com.example.travel.data.local.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TravelDatabase {
        return Room.databaseBuilder(
            context,
            TravelDatabase::class.java,
            TravelDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideUserDao(db: TravelDatabase): UserDao = db.userDao()

    @Provides
    fun provideTripNoteDao(db: TravelDatabase): TripNoteDao = db.tripNoteDao()

    @Provides
    fun provideExpenseDao(db: TravelDatabase): ExpenseDao = db.expenseDao()

    @Provides
    fun provideSearchDao(db: TravelDatabase): com.example.travel.data.local.db.dao.SearchDao = db.searchDao()

    @Provides
    fun provideNearbyDao(db: TravelDatabase): com.example.travel.data.local.db.dao.NearbyDao = db.nearbyDao()

    @Provides
    fun provideRouteDao(db: TravelDatabase): com.example.travel.data.local.db.dao.RouteDao = db.routeDao()

    @Provides
    fun provideOfflineRegionDao(db: TravelDatabase): com.example.travel.data.local.db.dao.OfflineRegionDao = db.offlineRegionDao()

    @Provides
    fun provideWeatherDao(db: TravelDatabase): com.example.travel.data.local.db.dao.WeatherDao = db.weatherDao()

    @Provides
    fun provideAiTripPlanDao(db: TravelDatabase): com.example.travel.data.local.db.dao.AiTripPlanDao = db.aiTripPlanDao()

    @Provides
    fun provideBudgetDao(db: TravelDatabase): com.example.travel.data.local.db.dao.BudgetDao = db.budgetDao()

    @Provides
    fun providePackingDao(db: TravelDatabase): com.example.travel.data.local.db.dao.PackingDao = db.packingDao()

    @Provides
    fun provideSafetyDao(db: TravelDatabase): com.example.travel.data.local.db.dao.SafetyDao = db.safetyDao()

    @Provides
    fun provideLocalGuideDao(db: TravelDatabase): com.example.travel.data.local.db.dao.LocalGuideDao = db.localGuideDao()

    @Provides
    fun provideGroupTripDao(db: TravelDatabase): com.example.travel.data.local.db.dao.GroupTripDao = db.groupTripDao()

    @Provides
    fun provideGroupExpenseDao(db: TravelDatabase): com.example.travel.data.local.db.dao.GroupExpenseDao = db.groupExpenseDao()

    @Provides
    fun provideFoodDao(db: TravelDatabase): com.example.travel.data.local.db.dao.FoodDao = db.foodDao()

    @Provides
    fun provideJournalDao(db: TravelDatabase): com.example.travel.data.local.db.dao.JournalDao = db.journalDao()

    @Provides
    fun providePhotoMemoryDao(db: TravelDatabase): com.example.travel.data.local.db.dao.PhotoMemoryDao = db.photoMemoryDao()

    @Provides
    fun provideTimelineDao(db: TravelDatabase): com.example.travel.data.local.db.dao.TimelineDao = db.timelineDao()

    @Provides
    fun provideMemoryCollectionDao(db: TravelDatabase): com.example.travel.data.local.db.dao.MemoryCollectionDao = db.memoryCollectionDao()
}
