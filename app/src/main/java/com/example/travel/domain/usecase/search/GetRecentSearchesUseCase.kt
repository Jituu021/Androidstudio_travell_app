package com.example.travel.domain.usecase.search

import com.example.travel.domain.model.SearchHistoryItem
import com.example.travel.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRecentSearchesUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    operator fun invoke(): Flow<List<SearchHistoryItem>> {
        return searchRepository.getRecentSearches()
    }
}
