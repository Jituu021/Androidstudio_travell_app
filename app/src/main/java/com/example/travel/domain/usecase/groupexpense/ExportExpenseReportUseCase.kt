package com.example.travel.domain.usecase.groupexpense

import android.content.Context
import com.example.travel.domain.model.GroupExpense
import com.example.travel.domain.model.Settlement
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class ExportExpenseReportUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(tripId: String, expenses: List<GroupExpense>, settlements: List<Settlement>): File? {
        return try {
            val csvDir = File(context.filesDir, "group_expense_exports").apply { mkdirs() }
            val file = File(csvDir, "Group_Settlement_${tripId}_${System.currentTimeMillis()}.csv")
            file.printWriter().use { out ->
                out.println("ID,Title,Category,Amount,PaidBy,SplitMethod")
                expenses.forEach { e ->
                    out.println("${e.id},\"${e.title}\",${e.category},${e.amount},${e.paidByName},${e.splitMethod}")
                }
                out.println("\n--- MINIMUM CASH FLOW SETTLEMENT PLAN ---")
                out.println("Payer,Payee,Amount,SettledStatus")
                settlements.forEach { s ->
                    out.println("${s.payerName},${s.payeeName},${s.amount},${if (s.isSettled) "Settled" else "Pending"}")
                }
            }
            file
        } catch (e: Exception) {
            null
        }
    }
}
