package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_collections")
data class MemoryCollectionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val category: String, // "SMART", "CUSTOM"
    val coverPhotoId: String = "",
    val isArchived: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "collection_items")
data class CollectionItemEntity(
    @PrimaryKey val id: String,
    val collectionId: String,
    val memoryId: String,
    val itemOrder: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "search_indexes")
data class SearchIndexEntity(
    @PrimaryKey val id: String,
    val memoryId: String,
    val memoryType: String, // "PHOTO", "JOURNAL", "FOOD", "PLACE"
    val searchableText: String,
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "trip_highlights")
data class TripHighlightEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val highlightTitle: String,
    val topMemoriesJson: String = "[]",
    val mostVisitedPlace: String = "",
    val favoriteRestaurant: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
