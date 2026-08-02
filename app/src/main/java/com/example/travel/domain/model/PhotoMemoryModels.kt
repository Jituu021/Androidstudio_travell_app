package com.example.travel.domain.model

data class PhotoMemory(
    val id: String = "",
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

data class PhotoMetadata(
    val photoId: String,
    val cameraMake: String = "Mobile Camera",
    val cameraModel: String = "HDR Lens",
    val iso: Int = 100,
    val fNumber: Double = 1.8,
    val exposureTime: String = "1/120s"
)
