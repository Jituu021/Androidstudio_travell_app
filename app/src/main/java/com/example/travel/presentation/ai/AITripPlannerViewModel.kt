package com.example.travel.presentation.ai

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.usecase.ai.GenerateTripPlanUseCase
import com.example.travel.domain.usecase.ai.GetTripPlanUseCase
import com.example.travel.domain.usecase.ai.SaveTripPlanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

data class AiTripUiState(
    val isLoading: Boolean = false,
    val activePlan: AiTripPlan? = null,
    val savedPlans: List<AiTripPlan> = emptyList(),
    val errorMessage: String? = null,
    val exportedPdfPath: String? = null
)

@HiltViewModel
class AITripPlannerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val generateTripPlanUseCase: GenerateTripPlanUseCase,
    private val saveTripPlanUseCase: SaveTripPlanUseCase,
    private val getTripPlanUseCase: GetTripPlanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AiTripUiState())
    val uiState: StateFlow<AiTripUiState> = _uiState.asStateFlow()

    init {
        loadSavedPlans()
    }

    fun generateItinerary(
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        travelers: Int,
        travelStyle: String,
        interests: List<String>
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = generateTripPlanUseCase(destination, startDate, endDate, budget, travelers, travelStyle, interests)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, activePlan = result.data)
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = result.message)
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun saveActivePlan() {
        val plan = _uiState.value.activePlan ?: return
        viewModelScope.launch {
            saveTripPlanUseCase(plan)
        }
    }

    private fun loadSavedPlans() {
        viewModelScope.launch {
            getTripPlanUseCase().collect { plans ->
                _uiState.value = _uiState.value.copy(savedPlans = plans)
            }
        }
    }

    fun exportItineraryPdf(plan: AiTripPlan): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val paint = Paint().apply {
                color = Color.BLACK
                textSize = 14f
            }

            val titlePaint = Paint().apply {
                color = Color.parseColor("#1A73E8")
                textSize = 20f
                isFakeBoldText = true
            }

            var y = 50f
            canvas.drawText("🗺 ${plan.destination} AI Itinerary", 40f, y, titlePaint)
            y += 30f
            canvas.drawText("Style: ${plan.travelStyle} • Budget: ₹${plan.budgetAmount.toInt()} • Travelers: ${plan.numberOfTravelers}", 40f, y, paint)
            y += 25f
            canvas.drawText("Summary: ${plan.summary}", 40f, y, paint)
            y += 35f

            plan.days.forEach { day ->
                paint.isFakeBoldText = true
                canvas.drawText("📌 ${day.title} (Est. ₹${day.dailyCostEstimate.toInt()})", 40f, y, paint)
                y += 20f
                paint.isFakeBoldText = false

                day.activities.forEach { act ->
                    canvas.drawText("  • ${act.timeSlot}: ${act.title} @ ${act.locationName}", 40f, y, paint)
                    y += 18f
                }
                y += 15f
            }

            pdfDoc.finishPage(page)

            val exportDir = File(context.filesDir, "exported_itineraries").apply { mkdirs() }
            val file = File(exportDir, "Itinerary_${plan.destination.replace(" ", "_")}_${System.currentTimeMillis()}.pdf")
            pdfDoc.writeTo(FileOutputStream(file))
            pdfDoc.close()

            _uiState.value = _uiState.value.copy(exportedPdfPath = file.absolutePath)
            Timber.d("Exported itinerary PDF to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Timber.e(e, "Error exporting itinerary PDF")
            null
        }
    }
}
