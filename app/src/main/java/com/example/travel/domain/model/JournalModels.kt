package com.example.travel.domain.model

data class Journal(
    val id: String = "",
    val title: String,
    val destination: String,
    val startDate: String,
    val endDate: String,
    val coverImageUrl: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class JournalEntry(
    val id: String = "",
    val journalId: String,
    val title: String,
    val content: String,
    val locationName: String = "",
    val mood: String = "Happy 😊",
    val weather: String = "Sunny ☀️",
    val tags: List<String> = emptyList(),
    val lastModified: Long = System.currentTimeMillis(),
    val timestamp: Long = System.currentTimeMillis()
)
