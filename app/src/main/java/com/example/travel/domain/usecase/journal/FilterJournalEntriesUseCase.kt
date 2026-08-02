package com.example.travel.domain.usecase.journal

import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FilterJournalEntriesUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    suspend operator fun invoke(journalId: String, mood: String?, tag: String?): Flow<List<JournalEntry>> {
        return travelJournalRepository.filterJournalEntries(journalId, mood, tag)
    }
}
