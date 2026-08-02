package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journals")
data class JournalEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val coverImageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val journalId: String,
    val title: String,
    val content: String,
    val locationName: String = "",
    val mood: String = "Happy 😊",
    val weather: String = "Sunny ☀️",
    val tagsJson: String = "[]",
    val lastModified: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis()
)
