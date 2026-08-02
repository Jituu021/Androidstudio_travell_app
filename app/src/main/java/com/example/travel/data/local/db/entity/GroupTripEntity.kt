package com.example.travel.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "group_trips")
data class GroupTripEntity(
    @PrimaryKey val id: String,
    val title: String,
    val destination: String,
    val ownerId: String,
    val inviteCode: String,
    val isArchived: Boolean = false,
    val memberCount: Int = 1,
    val lastSyncedTimestamp: Long = System.currentTimeMillis()
)
