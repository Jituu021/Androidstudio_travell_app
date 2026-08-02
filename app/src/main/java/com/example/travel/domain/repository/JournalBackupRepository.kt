package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import java.io.File

interface JournalBackupRepository {
    suspend fun createBackup(journalId: String, entries: List<JournalEntry>): Resource<File>
    suspend fun restoreBackup(backupFile: File): Resource<List<JournalEntry>>
}
