package com.example.travel.domain.model

data class JournalMedia(
    val id: String = "",
    val entryId: String,
    val mediaType: String, // "PHOTO", "VIDEO"
    val localFilePath: String,
    val mimeType: String = "image/jpeg",
    val timestamp: Long = System.currentTimeMillis()
)

data class JournalLocation(
    val id: String = "",
    val entryId: String,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
