package com.example.travel.domain.model

data class GuideAttraction(
    val name: String,
    val category: String, // "Must Visit", "Hidden Gem", "Photography", "Adventure"
    val description: String,
    val bestTimeOfDay: String = "Morning"
)

data class LocalGuide(
    val id: String = "",
    val destination: String,
    val historySummary: String,
    val cultureSummary: String,
    val localEtiquette: String,
    val bestMonths: String = "October to March",
    val languagesSpoken: String = "English, Local Native",
    val currency: String = "INR (₹) / USD ($)",
    val mustVisitPlaces: List<GuideAttraction> = emptyList(),
    val hiddenGems: List<GuideAttraction> = emptyList(),
    val festivals: List<String> = emptyList(),
    val isBookmarked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
