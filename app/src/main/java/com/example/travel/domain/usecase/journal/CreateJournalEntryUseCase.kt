package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import javax.inject.Inject

class CreateJournalEntryUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    suspend operator fun invoke(entry: JournalEntry): Resource<Boolean> {
        return travelJournalRepository.saveJournalEntry(entry)
    }
}
