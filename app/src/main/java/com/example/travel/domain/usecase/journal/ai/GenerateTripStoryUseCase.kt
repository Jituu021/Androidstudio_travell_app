package com.example.travel.domain.usecase.journal.ai

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AITripStory
import com.example.travel.domain.repository.AIJournalRepository
import javax.inject.Inject

class GenerateTripStoryUseCase @Inject constructor(
    private val aiJournalRepository: AIJournalRepository
) {
    suspend operator fun invoke(journalId: String, destination: String, summaryText: String): Resource<AITripStory> {
        return aiJournalRepository.generateTripStory(journalId, destination, summaryText)
    }
}
