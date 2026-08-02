package com.example.travel.domain.usecase.journal.export

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.JournalBackupRepository
import java.io.File
import javax.inject.Inject

class BackupJournalUseCase @Inject constructor(
    private val journalBackupRepository: JournalBackupRepository
) {
    suspend operator fun invoke(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return journalBackupRepository.createBackup(journalId, entries)
    }
}
