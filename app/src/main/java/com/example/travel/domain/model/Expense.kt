package com.example.travel.domain.model

data class Expense(
    val id: Long = 0,
    val category: String,
    val amount: Double,
    val currency: String = "USD",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
