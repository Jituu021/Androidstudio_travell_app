package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.PackingItem
import kotlinx.coroutines.flow.Flow

interface PackingRepository {
    fun getPackingItems(tripId: String): Flow<List<PackingItem>>
    suspend fun generatePackingList(
        destination: String,
        travelType: String,
        durationDays: Int,
        weatherCondition: String
    ): Resource<List<PackingItem>>
    suspend fun savePackingItem(item: PackingItem): Resource<Boolean>
    suspend fun updatePackedStatus(id: String, isPacked: Boolean): Resource<Boolean>
    suspend fun deletePackingItem(id: String): Resource<Boolean>
    suspend fun resetPackingList(tripId: String): Resource<Boolean>
}
