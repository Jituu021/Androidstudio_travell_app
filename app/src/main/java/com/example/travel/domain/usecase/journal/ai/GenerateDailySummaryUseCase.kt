package com.example.travel.domain.usecase.journal.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AIJournalSummary
import com.example.travel.domain.repository.AIJournalRepository
import javax.inject.Inject

class GenerateDailySummaryUseCase @Inject constructor(
    private val aiJournalRepository: AIJournalRepository
) {
    suspend operator fun invoke(journalId: String, date: String, entriesText: String): Resource<AIJournalSummary> {
        return aiJournalRepository.generateDailySummary(journalId, date, entriesText)
    }
}
