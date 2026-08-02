package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photo_memories",
    indices = [Index(value = ["tripId"]), Index(value = ["fileHash"], unique = true)]
)
data class PhotoMemoryEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val localFilePath: String,
    val fileHash: String,
    val captureTimestamp: Long = System.currentTimeMillis(),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val locationName: String = "",
    val orientation: Int = 0,
    val fileSizeBytes: Long = 0,
    val width: Int = 0,
    val height: Int = 0
)

@Entity(tableName = "photo_metadata")
data class PhotoMetadataEntity(
    @PrimaryKey val photoId: String,
    val cameraMake: String = "",
    val cameraModel: String = "",
    val iso: Int = 0,
    val fNumber: Double = 0.0,
    val exposureTime: String = ""
)
