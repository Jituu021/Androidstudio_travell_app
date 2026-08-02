package com.example.travel.di

import com.example.travel.data.repository.AuthRepositoryImpl
import com.example.travel.data.repository.GisRepositoryImpl
import com.example.travel.data.repository.TravelRepositoryImpl
import com.example.travel.domain.repository.AuthRepository
import com.example.travel.domain.repository.GisRepository
import com.example.travel.domain.repository.TravelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTravelRepository(
        impl: TravelRepositoryImpl
    ): TravelRepository

    @Binds
    @Singleton
    abstract fun bindGisRepository(
        impl: GisRepositoryImpl
    ): GisRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: com.example.travel.data.repository.LocationRepositoryImpl
    ): com.example.travel.domain.repository.LocationRepository

    @Binds
    @Singleton
    abstract fun bindSearchRepository(
        impl: com.example.travel.data.repository.SearchRepositoryImpl
    ): com.example.travel.domain.repository.SearchRepository

    @Binds
    @Singleton
    abstract fun bindNearbyRepository(
        impl: com.example.travel.data.repository.NearbyRepositoryImpl
    ): com.example.travel.domain.repository.NearbyRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(
        impl: com.example.travel.data.repository.RouteRepositoryImpl
    ): com.example.travel.domain.repository.RouteRepository

    @Binds
    @Singleton
    abstract fun bindNavigationRepository(
        impl: com.example.travel.data.repository.NavigationRepositoryImpl
    ): com.example.travel.domain.repository.NavigationRepository

    @Binds
    @Singleton
    abstract fun bindVoiceNavigationRepository(
        impl: com.example.travel.data.repository.VoiceNavigationRepositoryImpl
    ): com.example.travel.domain.repository.VoiceNavigationRepository

    @Binds
    @Singleton
    abstract fun bindOfflineMapRepository(
        impl: com.example.travel.data.repository.OfflineMapRepositoryImpl
    ): com.example.travel.domain.repository.OfflineMapRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        impl: com.example.travel.data.repository.WeatherRepositoryImpl
    ): com.example.travel.domain.repository.WeatherRepository

    @Binds
    @Singleton
    abstract fun bindAITripRepository(
        impl: com.example.travel.data.repository.AITripRepositoryImpl
    ): com.example.travel.domain.repository.AITripRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        impl: com.example.travel.data.repository.BudgetRepositoryImpl
    ): com.example.travel.domain.repository.BudgetRepository

    @Binds
    @Singleton
    abstract fun bindPackingRepository(
        impl: com.example.travel.data.repository.PackingRepositoryImpl
    ): com.example.travel.domain.repository.PackingRepository

    @Binds
    @Singleton
    abstract fun bindSafetyRepository(
        impl: com.example.travel.data.repository.SafetyRepositoryImpl
    ): com.example.travel.domain.repository.SafetyRepository

    @Binds
    @Singleton
    abstract fun bindLocalGuideRepository(
        impl: com.example.travel.data.repository.LocalGuideRepositoryImpl
    ): com.example.travel.domain.repository.LocalGuideRepository

    @Binds
    @Singleton
    abstract fun bindGroupTripRepository(
        impl: com.example.travel.data.repository.GroupTripRepositoryImpl
    ): com.example.travel.domain.repository.GroupTripRepository

    @Binds
    @Singleton
    abstract fun bindGroupExpenseRepository(
        impl: com.example.travel.data.repository.GroupExpenseRepositoryImpl
    ): com.example.travel.domain.repository.GroupExpenseRepository

    @Binds
    @Singleton
    abstract fun bindFoodRepository(
        impl: com.example.travel.data.repository.FoodRepositoryImpl
    ): com.example.travel.domain.repository.FoodRepository

    @Binds
    @Singleton
    abstract fun bindTravelJournalRepository(
        impl: com.example.travel.data.repository.TravelJournalRepositoryImpl
    ): com.example.travel.domain.repository.TravelJournalRepository

    @Binds
    @Singleton
    abstract fun bindJournalMediaRepository(
        impl: com.example.travel.data.repository.JournalMediaRepositoryImpl
    ): com.example.travel.domain.repository.JournalMediaRepository

    @Binds
    @Singleton
    abstract fun bindVoiceNoteRepository(
        impl: com.example.travel.data.repository.VoiceNoteRepositoryImpl
    ): com.example.travel.domain.repository.VoiceNoteRepository

    @Binds
    @Singleton
    abstract fun bindAIJournalRepository(
        impl: com.example.travel.data.repository.AIJournalRepositoryImpl
    ): com.example.travel.domain.repository.AIJournalRepository
}
