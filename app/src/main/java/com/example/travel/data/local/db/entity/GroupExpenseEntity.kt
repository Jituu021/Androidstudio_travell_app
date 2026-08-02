package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_expenses")
data class GroupExpenseEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val paidByUserId: String,
    val paidByName: String,
    val amount: Double,
    val title: String,
    val category: String,
    val splitMethod: String = "Equal", // "Equal", "Exact", "Percentage", "Shares"
    val timestamp: Long = System.currentTimeMillis()
)
