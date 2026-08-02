package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.MemoryCollectionDao
import com.example.travel.data.local.db.entity.MemoryCollectionEntity
import com.example.travel.data.local.db.entity.TripHighlightEntity
import com.example.travel.domain.model.MemoryCollection
import com.example.travel.domain.model.TripHighlight
import com.example.travel.domain.repository.MemoryCollectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryCollectionRepositoryImpl @Inject constructor(
    private val memoryCollectionDao: MemoryCollectionDao
) : MemoryCollectionRepository {

    override fun getAllCollections(): Flow<List<MemoryCollection>> {
        return memoryCollectionDao.getAllCollections().map { list ->
            list.map { e ->
                MemoryCollection(
                    id = e.id,
                    title = e.title,
                    category = e.category,
                    coverPhotoId = e.coverPhotoId,
                    isArchived = e.isArchived,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun createCollection(collection: MemoryCollection): Resource<Boolean> {
        return try {
            val entity = MemoryCollectionEntity(
                id = collection.id.ifEmpty { "col_${System.currentTimeMillis()}" },
                title = collection.title,
                category = collection.category,
                coverPhotoId = collection.coverPhotoId,
                isArchived = collection.isArchived
            )
            memoryCollectionDao.insertCollection(entity)
            Timber.d("Created Memory Collection: ${collection.title}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create collection", e)
        }
    }

    override fun getTripHighlight(tripId: String): Flow<TripHighlight?> {
        return memoryCollectionDao.getTripHighlight(tripId).map { entity ->
            entity?.let { e ->
                val topMemoriesList = mutableListOf<String>()
                try {
                    val arr = JSONArray(e.topMemoriesJson)
                    for (i in 0 until arr.length()) topMemoriesList.add(arr.getString(i))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                TripHighlight(
                    id = e.id,
                    tripId = e.tripId,
                    highlightTitle = e.highlightTitle,
                    topMemories = topMemoriesList,
                    mostVisitedPlace = e.mostVisitedPlace,
                    favoriteRestaurant = e.favoriteRestaurant,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun saveTripHighlight(highlight: TripHighlight): Resource<Boolean> {
        return try {
            val entity = TripHighlightEntity(
                id = highlight.id.ifEmpty { "highlight_${System.currentTimeMillis()}" },
                tripId = highlight.tripId,
                highlightTitle = highlight.highlightTitle,
                topMemoriesJson = JSONArray(highlight.topMemories).toString(),
                mostVisitedPlace = highlight.mostVisitedPlace,
                favoriteRestaurant = highlight.favoriteRestaurant
            )
            memoryCollectionDao.insertTripHighlight(entity)
            Timber.d("Saved Trip Highlight for ${highlight.tripId}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save trip highlight", e)
        }
    }
}
