package com.example.travel.domain.model

data class PackingItem(
    val id: String = "",
    val tripId: String = "default_trip",
    val category: String, // "Clothing", "Electronics", "Documents", "Toiletries", "Medicines", "Accessories", "Emergency Kit"
    val itemName: String,
    val isPacked: Boolean = false,
    val isEssential: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
