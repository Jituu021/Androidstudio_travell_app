package com.example.travel.domain.model

data class VoiceNote(
    val id: String = "",
    val entryId: String,
    val title: String,
    val localFilePath: String,
    val durationSeconds: Int,
    val fileSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)
