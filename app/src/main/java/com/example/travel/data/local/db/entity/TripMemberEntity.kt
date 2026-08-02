package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trip_members")
data class TripMemberEntity(
    @PrimaryKey val id: String,
    val tripId: String,
    val userId: String,
    val name: String,
    val role: String = "Member", // "Owner", "Admin", "Member"
    val isOnline: Boolean = true,
    val avatarUrl: String = "",
    val joinedTimestamp: Long = System.currentTimeMillis()
)
