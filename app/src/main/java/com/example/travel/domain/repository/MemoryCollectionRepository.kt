package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.MemoryCollection
import com.example.travel.domain.model.TripHighlight
import kotlinx.coroutines.flow.Flow

interface MemoryCollectionRepository {
    fun getAllCollections(): Flow<List<MemoryCollection>>
    suspend fun createCollection(collection: MemoryCollection): Resource<Boolean>
    fun getTripHighlight(tripId: String): Flow<TripHighlight?>
    suspend fun saveTripHighlight(highlight: TripHighlight): Resource<Boolean>
}
