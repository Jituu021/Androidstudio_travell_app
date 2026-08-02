package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.JournalDao
import com.example.travel.data.local.db.entity.JournalEntity
import com.example.travel.data.local.db.entity.JournalEntryEntity
import com.example.travel.domain.model.Journal
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TravelJournalRepositoryImpl @Inject constructor(
    private val journalDao: JournalDao
) : TravelJournalRepository {

    override fun getAllJournals(): Flow<List<Journal>> {
        return journalDao.getAllJournals().map { list ->
            list.map { e ->
                Journal(
                    id = e.id,
                    title = e.title,
                    destination = e.destination,
                    startDate = e.startDate,
                    endDate = e.endDate,
                    coverImageUrl = e.coverImageUrl,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override fun getJournalEntries(journalId: String): Flow<List<JournalEntry>> {
        return journalDao.getEntriesForJournal(journalId).map { list ->
            list.map { parseEntityToEntry(it) }
        }
    }

    override suspend fun createJournal(journal: Journal): Resource<Boolean> {
        return try {
            val entity = JournalEntity(
                id = journal.id.ifEmpty { "journal_${System.currentTimeMillis()}" },
                title = journal.title,
                destination = journal.destination,
                startDate = journal.startDate,
                endDate = journal.endDate,
                coverImageUrl = journal.coverImageUrl
            )
            journalDao.insertJournal(entity)
            Timber.d("Created Journal: ${journal.title}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create journal", e)
        }
    }

    override suspend fun saveJournalEntry(entry: JournalEntry): Resource<Boolean> {
        return try {
            val entity = JournalEntryEntity(
                id = entry.id.ifEmpty { "entry_${System.currentTimeMillis()}" },
                journalId = entry.journalId,
                title = entry.title,
                content = entry.content,
                locationName = entry.locationName,
                mood = entry.mood,
                weather = entry.weather,
                tagsJson = JSONArray(entry.tags).toString(),
                lastModified = System.currentTimeMillis()
            )
            journalDao.insertJournalEntry(entity)
            Timber.d("Saved Journal Entry: ${entry.title}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save journal entry", e)
        }
    }

    override suspend fun deleteJournalEntry(id: String): Resource<Boolean> {
        return try {
            journalDao.deleteJournalEntry(id)
            Timber.d("Deleted Journal Entry: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete journal entry", e)
        }
    }

    override suspend fun searchJournalEntries(journalId: String, query: String): Flow<List<JournalEntry>> {
        return getJournalEntries(journalId).map { list ->
            if (query.isBlank()) list else list.filter {
                it.title.contains(query, ignoreCase = true) || it.content.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun filterJournalEntries(
        journalId: String,
        mood: String?,
        tag: String?
    ): Flow<List<JournalEntry>> {
        return getJournalEntries(journalId).map { list ->
            list.filter { entry ->
                val moodMatch = mood.isNullOrBlank() || entry.mood.contains(mood, ignoreCase = true)
                val tagMatch = tag.isNullOrBlank() || entry.tags.any { it.contains(tag, ignoreCase = true) }
                moodMatch && tagMatch
            }
        }
    }

    private fun parseEntityToEntry(e: JournalEntryEntity): JournalEntry {
        val tagsList = mutableListOf<String>()
        try {
            val arr = JSONArray(e.tagsJson)
            for (i in 0 until arr.length()) tagsList.add(arr.getString(i))
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return JournalEntry(
            id = e.id,
            journalId = e.journalId,
            title = e.title,
            content = e.content,
            locationName = e.locationName,
            mood = e.mood,
            weather = e.weather,
            tags = tagsList,
            lastModified = e.lastModified,
            timestamp = e.timestamp
        )
    }
}
