package com.example.travel.data.remote

import com.example.travel.domain.model.AIJournalSummary
import com.example.travel.domain.model.AITripStory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiJournalDataSource @Inject constructor() {

    suspend fun generateDailySummary(journalId: String, date: String, entriesText: String): AIJournalSummary {
        return AIJournalSummary(
            id = "summary_${journalId}_$date",
            journalId = journalId,
            date = date,
            suggestedTitle = "Sunlit Exploration & Local Flavors",
            summaryText = "Today was filled with scenic walks through the historic town center, sampling authentic local cuisine, and capturing unforgettable sunset views.",
            dailyHighlights = listOf("Explored historic monuments", "Tasted regional claypot curry", "Watched sunset from the scenic lookout"),
            bestMoment = "Relaxing at the rooftop café during sunset",
            travelTip = "Visit popular monuments early in the morning to avoid long queues."
        )
    }

    suspend fun generateTripStory(journalId: String, destination: String, summaryText: String): AITripStory {
        return AITripStory(
            id = "story_$journalId",
            journalId = journalId,
            title = "Unforgettable Odyssey in $destination",
            fullStory = "Our journey to $destination was an incredible blend of culture, culinary delights, and breathtaking landscapes. Every day unveiled new adventures—from serene morning walks to vibrant evening markets."
        )
    }
}
