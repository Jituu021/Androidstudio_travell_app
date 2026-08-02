package com.example.travel.domain.usecase.journal

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.JournalLocation
import com.example.travel.domain.repository.JournalMediaRepository
import javax.inject.Inject

class LocationPickerUseCase @Inject constructor(
    private val journalMediaRepository: JournalMediaRepository
) {
    suspend operator fun invoke(location: JournalLocation): Resource<Boolean> {
        return journalMediaRepository.setLocation(location)
    }
}
