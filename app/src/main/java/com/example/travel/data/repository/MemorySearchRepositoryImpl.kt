package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.MemoryCollectionDao
import com.example.travel.data.local.db.entity.SearchIndexEntity
import com.example.travel.domain.model.SearchIndex
import com.example.travel.domain.repository.MemorySearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemorySearchRepositoryImpl @Inject constructor(
    private val memoryCollectionDao: MemoryCollectionDao
) : MemorySearchRepository {

    override fun searchMemories(query: String): Flow<List<SearchIndex>> {
        return memoryCollectionDao.searchMemories(query).map { list ->
            list.map { e ->
                SearchIndex(
                    id = e.id,
                    memoryId = e.memoryId,
                    memoryType = e.memoryType,
                    searchableText = e.searchableText,
                    tags = e.tags,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun indexMemory(searchIndex: SearchIndex): Resource<Boolean> {
        return try {
            val entity = SearchIndexEntity(
                id = searchIndex.id.ifEmpty { "idx_${System.currentTimeMillis()}" },
                memoryId = searchIndex.memoryId,
                memoryType = searchIndex.memoryType,
                searchableText = searchIndex.searchableText,
                tags = searchIndex.tags
            )
            memoryCollectionDao.insertSearchIndex(entity)
            Timber.d("Indexed memory: ${searchIndex.memoryId}")
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to index memory", e)
        }
    }
}
