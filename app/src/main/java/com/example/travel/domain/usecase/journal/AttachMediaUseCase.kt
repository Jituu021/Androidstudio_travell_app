package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalMedia
import com.example.travel.domain.repository.JournalMediaRepository
import javax.inject.Inject

class AttachMediaUseCase @Inject constructor(
    private val journalMediaRepository: JournalMediaRepository
) {
    suspend operator fun invoke(media: JournalMedia): Resource<Boolean> {
        return journalMediaRepository.attachMedia(media)
    }
}
