package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_media")
data class JournalMediaEntity(
    @PrimaryKey val id: String,
    val entryId: String,
    val mediaType: String, // "PHOTO", "VIDEO"
    val localFilePath: String,
    val mimeType: String = "image/jpeg",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_locations")
data class JournalLocationEntity(
    @PrimaryKey val id: String,
    val entryId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
