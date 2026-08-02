package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_guides")
data class LocalGuideEntity(
    @PrimaryKey val id: String,
    val destination: String,
    val historySummary: String,
    val cultureSummary: String,
    val localEtiquette: String,
    val bestMonths: String,
    val languagesSpoken: String,
    val currency: String,
    val mustVisitJson: String,
    val hiddenGemsJson: String,
    val festivalsJson: String,
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
