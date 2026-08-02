package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "voice_notes")
data class VoiceNoteEntity(
    @PrimaryKey val id: String,
    val entryId: String,
    val title: String,
    val localFilePath: String,
    val durationSeconds: Int,
    val fileSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis()
)
