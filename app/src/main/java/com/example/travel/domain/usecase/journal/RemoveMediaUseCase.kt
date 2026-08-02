package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.repository.JournalMediaRepository
import javax.inject.Inject

class RemoveMediaUseCase @Inject constructor(
    private val journalMediaRepository: JournalMediaRepository
) {
    suspend operator fun invoke(id: String): Resource<Boolean> {
        return journalMediaRepository.removeMedia(id)
    }
}
