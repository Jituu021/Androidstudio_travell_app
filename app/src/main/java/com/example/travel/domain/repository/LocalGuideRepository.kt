package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.LocalGuide
import kotlinx.coroutines.flow.Flow

interface LocalGuideRepository {
    fun getAllSavedGuides(): Flow<List<LocalGuide>>
    suspend fun generateLocalGuide(destination: String): Resource<LocalGuide>
    suspend fun saveGuide(guide: LocalGuide): Resource<Boolean>
    suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean): Resource<Boolean>
}
