package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Journal
import com.example.travel.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface TravelJournalRepository {
    fun getAllJournals(): Flow<List<Journal>>
    fun getJournalEntries(journalId: String): Flow<List<JournalEntry>>
    suspend fun createJournal(journal: Journal): Resource<Boolean>
    suspend fun saveJournalEntry(entry: JournalEntry): Resource<Boolean>
    suspend fun deleteJournalEntry(id: String): Resource<Boolean>
    suspend fun searchJournalEntries(journalId: String, query: String): Flow<List<JournalEntry>>
    suspend fun filterJournalEntries(journalId: String, mood: String?, tag: String?): Flow<List<JournalEntry>>
}
