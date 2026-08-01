package com.example.travel.domain.usecase.travel

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.TripNote
import com.example.travel.domain.repository.TravelRepository
import javax.inject.Inject

class AddTripNoteUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    suspend operator fun invoke(note: TripNote): Resource<Long> {
        if (note.title.isBlank()) return Resource.Error("Note title cannot be blank")
        return travelRepository.addTripNote(note)
    }
}
