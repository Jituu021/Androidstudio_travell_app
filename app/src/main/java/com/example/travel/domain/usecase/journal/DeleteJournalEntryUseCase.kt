package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.TravelJournalRepository
import javax.inject.Inject

class DeleteJournalEntryUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return travelJournalRepository.deleteJournalEntry(id)
    }
}
