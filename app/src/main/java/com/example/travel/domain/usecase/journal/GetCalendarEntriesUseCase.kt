package com.example.travel.domain.usecase.journal

import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GetCalendarEntriesUseCase @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository
) {
    suspend operator fun invoke(journalId: String): Flow<Map<String, Int>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return travelJournalRepository.getJournalEntries(journalId).map { entries ->
            entries.groupingBy { dateFormat.format(Date(it.timestamp)) }.eachCount()
        }
    }
}
