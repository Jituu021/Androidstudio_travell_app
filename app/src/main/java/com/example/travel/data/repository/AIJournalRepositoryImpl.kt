package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.JournalDao
import com.example.travel.data.local.db.entity.AIJournalSummaryEntity
import com.example.travel.data.local.db.entity.AITripStoryEntity
import com.example.travel.data.remote.GeminiJournalDataSource
import com.example.travel.domain.model.AIJournalSummary
import com.example.travel.domain.model.AITripStory
import com.example.travel.domain.repository.AIJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AIJournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao,
    private val geminiJournalDataSource: GeminiJournalDataSource
) : AIJournalRepository {

    override fun getDailySummary(journalId: String, date: String): Flow<AIJournalSummary?> {
        return journalDao.getAISummary(journalId, date).map { entity ->
            entity?.let { e ->
                val highlightsList = mutableListOf<String>()
                try {
                    val arr = JSONArray(e.dailyHighlightsJson)
                    for (i in 0 until arr.length()) highlightsList.add(arr.getString(i))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }

                AIJournalSummary(
                    id = e.id,
                    journalId = e.journalId,
                    date = e.date,
                    suggestedTitle = e.suggestedTitle,
                    summaryText = e.summaryText,
                    dailyHighlights = highlightsList,
                    bestMoment = e.bestMoment,
                    travelTip = e.travelTip,
                    promptVersion = e.promptVersion,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getTripStory(journalId: String): Flow<AITripStory?> {
        return journalDao.getAITripStory(journalId).map { entity ->
            entity?.let { e ->
                AITripStory(
                    id = e.id,
                    journalId = e.journalId,
                    title = e.title,
                    fullStory = e.fullStory,
                    promptVersion = e.promptVersion,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun generateDailySummary(
        journalId: String,
        date: String,
        entriesText: String
    ): Resource<AIJournalSummary> {
        return try {
            val summary = geminiJournalDataSource.generateDailySummary(journalId, date, entriesText)
            val entity = AIJournalSummaryEntity(
                id = summary.id,
                journalId = summary.journalId,
                date = summary.date,
                suggestedTitle = summary.suggestedTitle,
                summaryText = summary.summaryText,
                dailyHighlightsJson = JSONArray(summary.dailyHighlights).toString(),
                bestMoment = summary.bestMoment,
                travelTip = summary.travelTip,
                promptVersion = summary.promptVersion
            )
            journalDao.insertAISummary(entity)
            Timber.d("Generated & cached AI Daily Summary for $date")
            Resource.Success(summary)
        } catch (e: Exception) {
            Timber.e(e, "Error generating AI Daily Summary")
            Resource.Error(e.message ?: "Failed to generate AI Daily Summary", e)
        }
    }

    override suspend fun generateTripStory(
        journalId: String,
        destination: String,
        summaryText: String
    ): Resource<AITripStory> {
        return try {
            val story = geminiJournalDataSource.generateTripStory(journalId, destination, summaryText)
            val entity = AITripStoryEntity(
                id = story.id,
                journalId = story.journalId,
                title = story.title,
                fullStory = story.fullStory,
                promptVersion = story.promptVersion
            )
            journalDao.insertAITripStory(entity)
            Timber.d("Generated & cached AI Trip Story for $journalId")
            Resource.Success(story)
        } catch (e: Exception) {
            Timber.e(e, "Error generating AI Trip Story")
            Resource.Error(e.message ?: "Failed to generate AI Trip Story", e)
        }
    }
}
