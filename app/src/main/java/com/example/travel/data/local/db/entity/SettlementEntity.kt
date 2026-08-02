package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settlements")
data class SettlementEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val payerId: String,
    val payerName: String,
    val payeeId: String,
    val payeeName: String,
    val amount: Double,
    val isSettled: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
