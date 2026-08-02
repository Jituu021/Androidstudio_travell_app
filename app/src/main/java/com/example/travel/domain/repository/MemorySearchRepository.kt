package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SearchIndex
import kotlinx.coroutines.flow.Flow

interface MemorySearchRepository {
    fun searchMemories(query: String): Flow<List<SearchIndex>>
    suspend fun indexMemory(searchIndex: SearchIndex): Resource<Boolean>
}
