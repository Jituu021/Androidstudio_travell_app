package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.CollectionItemEntity
import com.example.travel.data.local.db.entity.MemoryCollectionEntity
import com.example.travel.data.local.db.entity.SearchIndexEntity
import com.example.travel.data.local.db.entity.TripHighlightEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryCollectionDao {
    @Query("SELECT * FROM memory_collections WHERE isArchived = 0 ORDER BY timestamp DESC")
    fun getAllCollections(): Flow<List<MemoryCollectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollection(collection: MemoryCollectionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectionItem(item: CollectionItemEntity)

    @Query("SELECT * FROM search_indexes WHERE searchableText LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchMemories(query: String): Flow<List<SearchIndexEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchIndex(index: SearchIndexEntity)

    @Query("SELECT * FROM trip_highlights WHERE tripId = :tripId LIMIT 1")
    fun getTripHighlight(tripId: String): Flow<TripHighlightEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTripHighlight(highlight: TripHighlightEntity)
}
