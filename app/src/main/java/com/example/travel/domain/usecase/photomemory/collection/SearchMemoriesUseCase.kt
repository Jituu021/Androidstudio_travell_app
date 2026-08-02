package com.example.travel.domain.usecase.photomemory.collection

import com.example.travel.domain.model.SearchIndex
import com.example.travel.domain.repository.MemorySearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchMemoriesUseCase @Inject constructor(
    private val memorySearchRepository: MemorySearchRepository
) {
    operator fun invoke(query: String): Flow<List<SearchIndex>> {
        return memorySearchRepository.searchMemories(query)
    }
}
