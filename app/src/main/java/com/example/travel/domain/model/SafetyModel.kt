package com.example.travel.domain.model

data class EmergencyService(
    val id: String = "",
    val name: String,
    val category: String, // "Hospital", "Police Station", "Pharmacy"
    val distanceKm: Double,
    val address: String = "",
    val phone: String = "112"
)

data class SafetyReport(
    val safetyScore: Int = 92,
    val riskLevel: String = "LOW", // "LOW", "MEDIUM", "HIGH"
    val riskReasons: List<String> = emptyList(),
    val precautions: List<String> = emptyList(),
    val emergencyServices: List<EmergencyService> = emptyList(),
    val nearestHospitalKm: Double = 1.2,
    val nearestPoliceKm: Double = 0.8,
    val timestamp: Long = System.currentTimeMillis()
)
