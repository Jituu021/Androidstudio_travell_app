package com.example.travel.data.repository

import android.content.Context
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.JournalExportRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalExportRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : JournalExportRepository {

    private val exportDir: File
        get() = File(context.filesDir, "journal_exports").apply { mkdirs() }

    override suspend fun exportToPdf(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return try {
            val file = File(exportDir, "Journal_${journalId}_${System.currentTimeMillis()}.pdf")
            file.writeText("PDF Journal Export\nTrip: $journalId\nTotal Entries: ${entries.size}\nGenerated locally.")
            Timber.d("Exported PDF: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export PDF", e)
        }
    }

    override suspend fun exportToMarkdown(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return try {
            val file = File(exportDir, "Journal_${journalId}_${System.currentTimeMillis()}.md")
            file.printWriter().use { out ->
                out.println("# Travel Journal: $journalId")
                out.println("Exported on: ${java.util.Date()}\n")
                entries.forEach { entry ->
                    out.println("## ${entry.title}")
                    out.println("**Mood:** ${entry.mood} | **Weather:** ${entry.weather}")
                    out.println("**Location:** ${entry.locationName}")
                    out.println("\n${entry.content}\n")
                    out.println("---")
                }
            }
            Timber.d("Exported Markdown: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export Markdown", e)
        }
    }

    override suspend fun exportToHtml(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return try {
            val file = File(exportDir, "Journal_${journalId}_${System.currentTimeMillis()}.html")
            file.printWriter().use { out ->
                out.println("<!DOCTYPE html><html><head><title>Travel Journal $journalId</title></head><body>")
                out.println("<h1>Travel Journal - $journalId</h1>")
                entries.forEach { entry ->
                    out.println("<div style='margin-bottom:20px; border:1px solid #ccc; padding:15px; border-radius:8px;'>")
                    out.println("<h2>${entry.title}</h2>")
                    out.println("<p><b>Mood:</b> ${entry.mood} | <b>Weather:</b> ${entry.weather}</p>")
                    out.println("<p>${entry.content}</p>")
                    out.println("</div>")
                }
                out.println("</body></html>")
            }
            Timber.d("Exported HTML: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export HTML", e)
        }
    }

    override suspend fun exportToJson(journalId: String, entries: List<JournalEntry>): Resource<File> {
        return try {
            val file = File(exportDir, "Journal_${journalId}_${System.currentTimeMillis()}.json")
            val jsonArray = JSONArray()
            entries.forEach { entry ->
                val obj = JSONObject().apply {
                    put("id", entry.id)
                    put("journalId", entry.journalId)
                    put("title", entry.title)
                    put("content", entry.content)
                    put("locationName", entry.locationName)
                    put("mood", entry.mood)
                    put("weather", entry.weather)
                    put("timestamp", entry.timestamp)
                }
                jsonArray.put(obj)
            }
            val root = JSONObject().apply {
                put("journalId", journalId)
                put("version", "1.0")
                put("entries", jsonArray)
            }
            file.writeText(root.toString(2))
            Timber.d("Exported JSON: ${file.absolutePath}")
            Resource.Success(file)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to export JSON", e)
        }
    }
}
