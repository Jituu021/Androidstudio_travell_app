package com.example.travel.domain.model

data class SearchHistoryItem(
    val id: Long = 0,
    val query: String,
    val timestamp: Long = System.currentTimeMillis()
)
