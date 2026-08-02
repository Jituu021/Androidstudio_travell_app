package com.example.travel.domain.usecase.journal

import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetJournalEntriesUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    operator fun invoke(journalId: String): Flow<List<JournalEntry>> {
        return travelJournalRepository.getJournalEntries(journalId)
    }
}
