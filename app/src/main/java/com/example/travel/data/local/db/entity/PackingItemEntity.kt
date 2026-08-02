package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "packing_items")
data class PackingItemEntity(
    @PrimaryKey val id: String,
    val tripId: String = "default_trip",
    val category: String,
    val itemName: String,
    val isPacked: Boolean = false,
    val isEssential: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
