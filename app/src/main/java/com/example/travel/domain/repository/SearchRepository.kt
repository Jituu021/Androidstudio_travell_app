package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SearchHistoryItem
import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchPlaces(query: String, userLat: Double, userLon: Double): Resource<List<MapLocation>>
    suspend fun reverseGeocode(userLat: Double, userLon: Double): Resource<String>
    fun getRecentSearches(): Flow<List<SearchHistoryItem>>
    suspend fun saveSearchHistory(query: String)
    suspend fun clearSearchHistory()
    suspend fun deleteSearchHistoryItem(id: Long)
    fun getFavoritePlaces(): Flow<List<MapLocation>>
    suspend fun toggleFavoritePlace(place: MapLocation): Resource<Boolean>
}
