package com.example.travel.presentation.nexus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.Expense
import com.example.travel.domain.model.TripNote
import com.example.travel.domain.usecase.travel.AddExpenseUseCase
import com.example.travel.domain.usecase.travel.AddTripNoteUseCase
import com.example.travel.domain.usecase.travel.CalculateSunriseSunsetUseCase
import com.example.travel.domain.usecase.travel.DeleteTripNoteUseCase
import com.example.travel.domain.usecase.travel.GetExpensesUseCase
import com.example.travel.domain.usecase.travel.GetTripNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class NexusGuideUiState(
    val sunriseTime: String = "--:--",
    val sunsetTime: String = "--:--",
    val statusMessage: String? = null
)

@HiltViewModel
class NexusGuideViewModel @Inject constructor(
    private val getTripNotesUseCase: GetTripNotesUseCase,
    private val addTripNoteUseCase: AddTripNoteUseCase,
    private val deleteTripNoteUseCase: DeleteTripNoteUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val calculateSunriseSunsetUseCase: CalculateSunriseSunsetUseCase
) : ViewModel() {

    val tripNotes: StateFlow<List<TripNote>> = getTripNotesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val expenses: StateFlow<List<Expense>> = getExpensesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(NexusGuideUiState())
    val uiState: StateFlow<NexusGuideUiState> = _uiState.asStateFlow()

    fun calculateSolarTimes(lat: Double, lon: Double) {
        val (sunrise, sunset) = calculateSunriseSunsetUseCase(lat, lon)
        _uiState.value = _uiState.value.copy(sunriseTime = sunrise, sunsetTime = sunset)
        Timber.d("Calculated Sunrise: $sunrise, Sunset: $sunset for lat: $lat, lon: $lon")
    }

    fun addNote(title: String, content: String, tag: String) {
        viewModelScope.launch {
            addTripNoteUseCase(TripNote(title = title, content = content, tag = tag))
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            deleteTripNoteUseCase(id)
        }
    }

    fun addExpense(category: String, amount: Double, note: String) {
        viewModelScope.launch {
            addExpenseUseCase(Expense(category = category, amount = amount, note = note))
        }
    }
}
