package com.example.travel.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.Journal
import com.example.travel.domain.model.JournalEntry
import com.example.travel.domain.repository.TravelJournalRepository
import com.example.travel.domain.usecase.journal.CreateJournalEntryUseCase
import com.example.travel.domain.usecase.journal.DeleteJournalEntryUseCase
import com.example.travel.domain.usecase.journal.FilterJournalEntriesUseCase
import com.example.travel.domain.usecase.journal.GetJournalEntriesUseCase
import com.example.travel.domain.usecase.journal.SaveJournalEntryUseCase
import com.example.travel.domain.usecase.journal.SearchJournalEntriesUseCase
import com.example.travel.domain.usecase.journal.UpdateJournalEntryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JournalUiState(
    val journals: List<Journal> = emptyList(),
    val activeEntries: List<JournalEntry> = emptyList(),
    val filteredEntries: List<JournalEntry> = emptyList(),
    val searchQuery: String = "",
    val selectedMoodFilter: String? = null,
    val selectedTagFilter: String? = null,
    val isLoading: Boolean = false,
    val activeJournalId: String = "default_journal"
)

@HiltViewModel
class TravelJournalViewModel @Inject constructor(
    private val travelJournalRepository: TravelJournalRepository,
    private val createJournalEntryUseCase: CreateJournalEntryUseCase,
    private val getJournalEntriesUseCase: GetJournalEntriesUseCase,
    private val deleteJournalEntryUseCase: DeleteJournalEntryUseCase,
    private val updateJournalEntryUseCase: UpdateJournalEntryUseCase,
    private val saveJournalEntryUseCase: SaveJournalEntryUseCase,
    private val searchJournalEntriesUseCase: SearchJournalEntriesUseCase,
    private val filterJournalEntriesUseCase: FilterJournalEntriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JournalUiState())
    val uiState: StateFlow<JournalUiState> = _uiState.asStateFlow()

    init {
        loadJournals()
    }

    private fun loadJournals() {
        viewModelScope.launch {
            travelJournalRepository.getAllJournals().collect { list ->
                _uiState.value = _uiState.value.copy(journals = list)
                if (list.isNotEmpty()) {
                    loadEntriesForJournal(list.first().id)
                }
            }
        }
    }

    fun loadEntriesForJournal(journalId: String) {
        viewModelScope.launch {
            getJournalEntriesUseCase(journalId).collect { entries ->
                _uiState.value = _uiState.value.copy(
                    activeEntries = entries,
                    filteredEntries = entries,
                    activeJournalId = journalId
                )
            }
        }
    }

    fun saveEntry(id: String = "", title: String, content: String, location: String, mood: String, weather: String, tags: List<String>) {
        viewModelScope.launch {
            val entry = JournalEntry(
                id = id.ifEmpty { "entry_${System.currentTimeMillis()}" },
                journalId = _uiState.value.activeJournalId,
                title = title,
                content = content,
                locationName = location,
                mood = mood,
                weather = weather,
                tags = tags,
                lastModified = System.currentTimeMillis()
            )
            saveJournalEntryUseCase(entry)
        }
    }

    fun searchEntries(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            searchJournalEntriesUseCase(_uiState.value.activeJournalId, query).collect { list ->
                _uiState.value = _uiState.value.copy(filteredEntries = list)
            }
        }
    }

    fun filterEntries(mood: String?, tag: String?) {
        _uiState.value = _uiState.value.copy(selectedMoodFilter = mood, selectedTagFilter = tag)
        viewModelScope.launch {
            filterJournalEntriesUseCase(_uiState.value.activeJournalId, mood, tag).collect { list ->
                _uiState.value = _uiState.value.copy(filteredEntries = list)
            }
        }
    }

    fun removeEntry(id: String) {
        viewModelScope.launch {
            deleteJournalEntryUseCase(id)
        }
    }
}
