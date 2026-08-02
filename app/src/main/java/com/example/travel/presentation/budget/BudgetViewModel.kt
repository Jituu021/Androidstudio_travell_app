package com.example.travel.presentation.budget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.BudgetPrediction
import com.example.travel.domain.model.Expense
import com.example.travel.domain.model.TripBudget
import com.example.travel.domain.usecase.budget.CreateBudgetUseCase
import com.example.travel.domain.usecase.budget.PredictBudgetUseCase
import com.example.travel.domain.usecase.travel.AddExpenseUseCase
import com.example.travel.domain.usecase.travel.DeleteExpenseUseCase
import com.example.travel.domain.usecase.travel.GetExpensesUseCase
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

data class BudgetUiState(
    val activeBudget: TripBudget = TripBudget(tripName = "Current Trip", totalBudget = 25000.0, currencySymbol = "₹", totalDays = 5),
    val expenses: List<Expense> = emptyList(),
    val totalSpent: Double = 0.0,
    val remainingBudget: Double = 25000.0,
    val prediction: BudgetPrediction? = null,
    val csvExportPath: String? = null,
    val pdfExportPath: String? = null
)

@HiltViewModel
class BudgetViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val createBudgetUseCase: CreateBudgetUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val predictBudgetUseCase: PredictBudgetUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        viewModelScope.launch {
            getExpensesUseCase().collect { list ->
                val spent = list.sumOf { it.amount }
                val rem = (_uiState.value.activeBudget.totalBudget - spent).coerceAtLeast(0.0)
                _uiState.value = _uiState.value.copy(
                    expenses = list,
                    totalSpent = spent,
                    remainingBudget = rem
                )
                recalculatePrediction()
            }
        }
    }

    fun addExpense(category: String, amount: Double, note: String) {
        viewModelScope.launch {
            val exp = Expense(category = category, amount = amount, note = note)
            addExpenseUseCase(exp)
        }
    }

    fun removeExpense(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense.id)
        }
    }

    fun recalculatePrediction() {
        viewModelScope.launch {
            val pred = predictBudgetUseCase(
                totalBudget = _uiState.value.activeBudget.totalBudget,
                totalSpent = _uiState.value.totalSpent,
                daysElapsed = 2,
                totalTripDays = _uiState.value.activeBudget.totalDays
            )
            _uiState.value = _uiState.value.copy(prediction = pred)
        }
    }

    fun exportExpensesCsv(): File? {
        return try {
            val csvDir = File(context.filesDir, "exported_csv").apply { mkdirs() }
            val file = File(csvDir, "Expenses_${System.currentTimeMillis()}.csv")
            file.printWriter().use { out ->
                out.println("ID,Category,Amount,Note,Timestamp")
                _uiState.value.expenses.forEach { e ->
                    out.println("${e.id},${e.category},${e.amount},\"${e.note}\",${e.timestamp}")
                }
            }
            _uiState.value = _uiState.value.copy(csvExportPath = file.absolutePath)
            Timber.d("Exported expenses CSV to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Timber.e(e, "Error exporting CSV")
            null
        }
    }

    fun exportExpensesPdf(): File? {
        return try {
            val pdfDoc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
            val page = pdfDoc.startPage(pageInfo)
            val canvas: Canvas = page.canvas

            val titlePaint = Paint().apply {
                color = Color.parseColor("#1A73E8")
                textSize = 20f
                isFakeBoldText = true
            }

            val textPaint = Paint().apply {
                color = Color.BLACK
                textSize = 13f
            }

            var y = 50f
            canvas.drawText("📊 Trip Budget & Expense Statement", 40f, y, titlePaint)
            y += 30f
            canvas.drawText("Total Budget: ₹${_uiState.value.activeBudget.totalBudget.toInt()} | Spent: ₹${_uiState.value.totalSpent.toInt()} | Remaining: ₹${_uiState.value.remainingBudget.toInt()}", 40f, y, textPaint)
            y += 30f

            _uiState.value.expenses.forEach { e ->
                canvas.drawText("• [${e.category}] ₹${e.amount.toInt()} - ${e.note}", 40f, y, textPaint)
                y += 20f
            }

            pdfDoc.finishPage(page)

            val exportDir = File(context.filesDir, "exported_pdf").apply { mkdirs() }
            val file = File(exportDir, "Budget_Statement_${System.currentTimeMillis()}.pdf")
            pdfDoc.writeTo(FileOutputStream(file))
            pdfDoc.close()

            _uiState.value = _uiState.value.copy(pdfExportPath = file.absolutePath)
            Timber.d("Exported budget PDF to: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            Timber.e(e, "Error exporting budget PDF")
            null
        }
    }
}
