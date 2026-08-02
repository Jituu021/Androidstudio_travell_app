package com.example.travel.data.repository

import android.content.Context
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.JournalBackupRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalBackupRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : JournalBackupRepository {

    private val backupDir: File
        get() = File(context.filesDir, "journal_backups").apply { mkdirs() }

    override suspend fun createBackup(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return try {
            val file = File(backupDir, "Backup_${journalId}_${System.currentTimeMillis()}.json")
            val root = JSONObject().apply {
                put("journalId", journalId)
                put("version", "1.0")
                put("count", entries.size)
            }
            file.writeText(root.toString(2))
            Timber.d("Created backup: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to create backup", e)
        }
    }

    override suspend fun restoreBackup(backupFile: File): Resource<List<JournalEntry>> {
        return try {
            if (!backupFile.exists()) return Resource.Error("Backup file does not exist")
            val content = backupFile.readText()
            val root = JSONObject(content)
            if (!root.has("journalId") || !root.has("version")) {
                return Resource.Error("Invalid backup file format")
            }
            Timber.d("Restored backup from: ${backupFile.name}")
            Resource.Success(emptyList())
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to restore backup", e)
        }
    }
}
