package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_journal_summaries")
data class AIJournalSummaryEntity(
    @PrimaryKey val id: String,
    val journalId: String,
    val date: String,
    val suggestedTitle: String,
    val summaryText: String,
    val dailyHighlightsJson: String = "[]",
    val bestMoment: String = "",
    val travelTip: String = "",
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_trip_stories")
data class AITripStoryEntity(
    @PrimaryKey val id: String,
    val journalId: String,
    val title: String,
    val fullStory: String,
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)
