package com.example.travel.domain.model

data class TripNote(
    val id: Long = 0,
    val title: String,
    val content: String,
    val tag: String = "General",
    val timestamp: Long = System.currentTimeMillis()
)
