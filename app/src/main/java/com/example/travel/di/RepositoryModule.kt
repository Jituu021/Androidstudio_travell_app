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
}
