package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AIJournalSummary
import com.example.travel.domain.model.AITripStory
import kotlinx.coroutines.flow.Flow

interface AIJournalRepository {
    fun getDailySummary(journalId: String, date: String): Flow<AIJournalSummary?>
    fun getTripStory(journalId: String): Flow<AITripStory?>
    suspend fun generateDailySummary(journalId: String, date: String, entriesText: String): Resource<AIJournalSummary>
    suspend fun generateTripStory(journalId: String, destination: String, summaryText: String): Resource<AITripStory>
}
