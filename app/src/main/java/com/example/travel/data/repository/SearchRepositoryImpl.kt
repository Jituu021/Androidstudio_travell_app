package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.SearchDao
import com.example.travel.data.local.db.entity.FavoritePlaceEntity
import com.example.travel.data.local.db.entity.SearchCacheEntity
import com.example.travel.data.local.db.entity.SearchHistoryEntity
import com.example.travel.data.remote.SearchRemoteDataSource
import com.example.travel.domain.model.SearchHistoryItem
import com.example.travel.domain.repository.SearchRepository
import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val searchRemoteDataSource: SearchRemoteDataSource,
    private val searchDao: SearchDao
) : SearchRepository {

    override suspend fun searchPlaces(
        query: String,
        userLat: Double,
        userLon: Double
    ): Resource<List<MapLocation>> {
        return try {
            val q = query.trim()
            if (q.isBlank()) return Resource.Success(emptyList())

            // 1. Try Photon remote search
            var remoteResults = searchRemoteDataSource.searchPhoton(q, userLat, userLon)
            if (remoteResults.isEmpty()) {
                remoteResults = searchRemoteDataSource.searchNominatim(q, userLat, userLon)
            }

            if (remoteResults.isNotEmpty()) {
                // Rank results by distance to current user location
                val rankedResults = remoteResults.map { place ->
                    val dist = calculateDistanceMeters(userLat, userLon, place.latitude, place.longitude)
                    place.copy(distanceMeters = dist)
                }.sortedBy { it.distanceMeters }

                // Cache in Room
                val cacheEntities = rankedResults.map { p ->
                    SearchCacheEntity(
                        id = p.id,
                        queryKey = q.lowercase(),
                        name = p.name,
                        address = p.address,
                        latitude = p.latitude,
                        longitude = p.longitude,
                        category = p.category,
                        rating = p.rating
                    )
                }
                searchDao.insertSearchCache(cacheEntities)
                Timber.d("Fetched & ranked ${rankedResults.size} search places for: $q")
                Resource.Success(rankedResults)
            } else {
                // Try offline cache fallback
                val cached = searchDao.getCachedSearchResults(q.lowercase())
                if (cached.isNotEmpty()) {
                    val offlinePlaces = cached.map { c ->
                        MapLocation(
                            id = c.id,
                            name = c.name,
                            address = c.address,
                            latitude = c.latitude,
                            longitude = c.longitude,
                            category = c.category,
                            rating = c.rating
                        )
                    }
                    Timber.d("Loaded ${offlinePlaces.size} cached search places for: $q")
                    Resource.Success(offlinePlaces)
                } else {
                    Resource.Success(emptyList())
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Error searching places for query: $query")
            // Try offline cache fallback on exception
            val cached = searchDao.getCachedSearchResults(query.trim().lowercase())
            if (cached.isNotEmpty()) {
                val offlinePlaces = cached.map { c ->
                    MapLocation(
                        id = c.id,
                        name = c.name,
                        address = c.address,
                        latitude = c.latitude,
                        longitude = c.longitude,
                        category = c.category,
                        rating = c.rating
                    )
                }
                Resource.Success(offlinePlaces)
            } else {
                Resource.Error(e.message ?: "Failed to search places", e)
            }
        }
    }

    override suspend fun reverseGeocode(userLat: Double, userLon: Double): Resource<String> {
        return try {
            val address = searchRemoteDataSource.reverseGeocode(userLat, userLon)
            Resource.Success(address)
        } catch (e: Exception) {
            Timber.e(e, "Error reverse geocoding")
            Resource.Error(e.message ?: "Failed to reverse geocode", e)
        }
    }

    override fun getRecentSearches(): Flow<List<SearchHistoryItem>> {
        return searchDao.getRecentSearches().map { entities ->
            entities.map { SearchHistoryItem(it.id, it.query, it.timestamp) }
        }
    }

    override suspend fun saveSearchHistory(query: String) {
        if (query.isNotBlank()) {
            searchDao.insertSearchHistory(SearchHistoryEntity(query = query.trim()))
            Timber.d("Saved search history item: $query")
        }
    }

    override suspend fun clearSearchHistory() {
        searchDao.clearSearchHistory()
        Timber.d("Cleared all search history")
    }

    override suspend fun deleteSearchHistoryItem(id: Long) {
        searchDao.deleteSearchHistoryItem(id)
        Timber.d("Deleted search history item: $id")
    }

    override fun getFavoritePlaces(): Flow<List<MapLocation>> {
        return searchDao.getFavoritePlaces().map { entities ->
            entities.map { f ->
                MapLocation(
                    id = f.id,
                    name = f.name,
                    address = f.address,
                    latitude = f.latitude,
                    longitude = f.longitude,
                    category = f.category,
                    rating = f.rating,
                    isFavorite = true
                )
            }
        }
    }

    override suspend fun toggleFavoritePlace(place: MapLocation): Resource<Boolean> {
        return try {
            val exists = searchDao.isFavoritePlace(place.id)
            if (exists) {
                searchDao.deleteFavoritePlace(place.id)
                Timber.d("Removed favorite place: ${place.name}")
                Resource.Success(false)
            } else {
                searchDao.insertFavoritePlace(
                    FavoritePlaceEntity(
                        id = place.id,
                        name = place.name,
                        address = place.address,
                        latitude = place.latitude,
                        longitude = place.longitude,
                        category = place.category,
                        rating = place.rating
                    )
                )
                Timber.d("Added favorite place: ${place.name}")
                Resource.Success(true)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite place")
            Resource.Error(e.message ?: "Failed to toggle favorite place", e)
        }
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
