package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalLocation
import com.example.travel.domain.model.JournalMedia
import kotlinx.coroutines.flow.Flow

interface JournalMediaRepository {
    fun getMediaForEntry(entryId: String): Flow<List<JournalMedia>>
    fun getLocationForEntry(entryId: String): Flow<JournalLocation?>
    suspend fun attachMedia(media: JournalMedia): Resource<Boolean>
    suspend fun removeMedia(id: String): Resource<Boolean>
    suspend fun setLocation(location: JournalLocation): Resource<Boolean>
}
