package com.example.travel.domain.usecase.search

import com.example.travel.domain.repository.SearchRepository
import javax.inject.Inject

class SaveSearchHistoryUseCase @Inject constructor(
    private val searchRepository: SearchRepository
) {
    suspend operator fun invoke(query: String) {
        searchRepository.saveSearchHistory(query)
    }
}
