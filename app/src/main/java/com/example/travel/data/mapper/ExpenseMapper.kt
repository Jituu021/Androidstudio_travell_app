package com.example.travel.data.mapper

import com.example.travel.data.local.db.entity.ExpenseEntity
import com.example.travel.domain.model.Expense

fun ExpenseEntity.toDomain(): Expense {
    return Expense(
        id = id,
        category = category,
        amount = amount,
        currency = currency,
        note = note,
        timestamp = timestamp
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        category = category,
        amount = amount,
        currency = currency,
        note = note,
        timestamp = timestamp
    )
}
