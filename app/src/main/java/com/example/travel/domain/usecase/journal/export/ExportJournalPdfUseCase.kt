package com.example.travel.domain.usecase.journal.export

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.JournalExportRepository
import java.io.File
import javax.inject.Inject

class ExportJournalPdfUseCase @Inject constructor(
    private val journalExportRepository: JournalExportRepository
) {
    suspend operator fun invoke(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return journalExportRepository.exportToPdf(journalId, entries)
    }
}
