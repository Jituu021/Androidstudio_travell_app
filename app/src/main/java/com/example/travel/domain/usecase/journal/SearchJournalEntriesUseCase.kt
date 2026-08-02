package com.example.travel.domain.usecase.journal

import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchJournalEntriesUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    suspend operator fun invoke(journalId: String, query: String): Flow<List<JournalEntry>> {
        return travelJournalRepository.searchJournalEntries(journalId, query)
    }
}
