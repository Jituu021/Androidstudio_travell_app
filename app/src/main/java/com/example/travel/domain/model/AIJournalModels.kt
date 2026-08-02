package com.example.travel.domain.model

data class AIJournalSummary(
    val id: String = "",
    val journalId: String,
    val date: String,
    val suggestedTitle: String,
    val summaryText: String,
    val dailyHighlights: List<String> = emptyList(),
    val bestMoment: String = "",
    val travelTip: String = "",
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)

data class AITripStory(
    val id: String = "",
    val journalId: String,
    val title: String,
    val fullStory: String,
    val promptVersion: String = "v1.0",
    val timestamp: Long = System.currentTimeMillis()
)
