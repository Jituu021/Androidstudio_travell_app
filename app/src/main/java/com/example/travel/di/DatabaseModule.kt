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
}
