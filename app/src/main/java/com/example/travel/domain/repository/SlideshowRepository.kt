package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.SlideshowProject
import kotlinx.coroutines.flow.Flow

interface SlideshowRepository {
    fun getSlideshowForTrip(tripId: String): Flow<SlideshowProject?>
    suspend fun createSlideshow(project: SlideshowProject): Resource<Boolean>
}
