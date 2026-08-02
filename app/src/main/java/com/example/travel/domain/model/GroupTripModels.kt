package com.example.travel.domain.model

data class TripMember(
    val id: String = "",
    val tripId: String,
    val userId: String,
    val name: String,
    val role: String = "Member", // "Owner", "Admin", "Member"
    val isOnline: Boolean = true,
    val avatarUrl: String = ""
)

data class GroupTrip(
    val id: String = "",
    val title: String,
    val destination: String,
    val ownerId: String,
    val inviteCode: String = "",
    val isArchived: Boolean = false,
    val members: List<TripMember> = emptyList(),
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
)
