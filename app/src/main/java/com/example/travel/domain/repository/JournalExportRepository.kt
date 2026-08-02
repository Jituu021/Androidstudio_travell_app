package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import java.io.File

interface JournalExportRepository {
    suspend fun exportToPdf(journalId: String, entries: List<JournalEntry>): Resource<File>
    suspend fun exportToMarkdown(journalId: String, entries: List<JournalEntry>): Resource<File>
    suspend fun exportToHtml(journalId: String, entries: List<JournalEntry>): Resource<File>
    suspend fun exportToJson(journalId: String, entries: List<JournalEntry>): Resource<File>
}
