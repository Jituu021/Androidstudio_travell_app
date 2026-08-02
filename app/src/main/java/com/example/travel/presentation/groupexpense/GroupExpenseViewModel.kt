package com.example.travel.presentation.groupexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.model.Settlement
import com.example.travel.domain.model.TripMember
import com.example.travel.domain.repository.GroupExpenseRepository
import com.example.travel.domain.usecase.groupexpense.AddGroupExpenseUseCase
import com.example.travel.domain.usecase.groupexpense.CalculateSettlementUseCase
import com.example.travel.domain.usecase.groupexpense.ExportExpenseReportUseCase
import com.example.travel.domain.usecase.groupexpense.SettlePaymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class GroupExpenseUiState(
    val expenses: List<GroupExpense> = emptyList(),
    val settlements: List<Settlement> = emptyList(),
    val totalExpenseAmount: Double = 0.0,
    val exportedFile: File? = null,
    val currentTripId: String = "default_trip"
)

@HiltViewModel
class GroupExpenseViewModel @Inject constructor(
    private val groupExpenseRepository: GroupExpenseRepository,
    private val addGroupExpenseUseCase: AddGroupExpenseUseCase,
    private val calculateSettlementUseCase: CalculateSettlementUseCase,
    private val settlePaymentUseCase: SettlePaymentUseCase,
    private val exportExpenseReportUseCase: ExportExpenseReportUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupExpenseUiState())
    val uiState: StateFlow<GroupExpenseUiState> = _uiState.asStateFlow()

    fun loadExpenses(tripId: String, members: List<TripMember>) {
        viewModelScope.launch {
            groupExpenseRepository.getExpensesForTrip(tripId).collect { list ->
                val total = list.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(
                    expenses = list,
                    totalExpenseAmount = total,
                    currentTripId = tripId
                )
                recalculateSettlements(tripId, members, list)
            }
        }
    }

    fun addExpense(title: String, category: String, amount: Double, paidByUserId: String, paidByName: String, splitMethod: String, members: List<TripMember>) {
        viewModelScope.launch {
            val exp = GroupExpense(
                id = "gexp_${System.currentTimeMillis()}",
                tripId = _uiState.value.currentTripId,
                paidByUserId = paidByUserId,
                paidByName = paidByName,
                amount = amount,
                title = title,
                category = category,
                splitMethod = splitMethod
            )
            addGroupExpenseUseCase(exp)
        }
    }

    private fun recalculateSettlements(tripId: String, members: List<TripMember>, expenses: List<GroupExpense>) {
        viewModelScope.launch {
            val result = calculateSettlementUseCase(tripId, members, expenses)
            if (result is com.example.travel.core.common.result.Resource.Success) {
                _uiState.value = _uiState.value.copy(settlements = result.data)
            }
        }
    }

    fun markSettled(settlementId: String, currentStatus: Boolean) {
        viewModelScope.launch {
            settlePaymentUseCase(settlementId, !currentStatus)
        }
    }

    fun exportReport(): File? {
        val file = exportExpenseReportUseCase(
            _uiState.value.currentTripId,
            _uiState.value.expenses,
            _uiState.value.settlements
        )
        _uiState.value = _uiState.value.copy(exportedFile = file)
        return file
    }
}
