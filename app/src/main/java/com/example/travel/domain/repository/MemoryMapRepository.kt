package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.MemoryRoute
import kotlinx.coroutines.flow.Flow

interface MemoryMapRepository {
    fun getMemoryRoute(tripId: String): Flow<MemoryRoute?>
    suspend fun saveMemoryRoute(route: MemoryRoute): Resource<Boolean>
}
