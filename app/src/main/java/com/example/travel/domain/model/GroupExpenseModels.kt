package com.example.travel.domain.model

data class GroupExpense(
    val id: String = "",
    val tripId: String,
    val paidByUserId: String,
    val paidByName: String,
    val amount: Double,
    val title: String,
    val category: String,
    val splitMethod: String = "Equal",
    val timestamp: Long = System.currentTimeMillis()
)

data class Settlement(
    val id: String = "",
    val tripId: String,
    val payerId: String,
    val payerName: String,
    val payeeId: String,
    val payeeName: String,
    val amount: Double,
    val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
