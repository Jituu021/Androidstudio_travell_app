package com.example.travel.domain.usecase.travel

import com.example.travel.domain.model.TripNote
import com.example.travel.domain.repository.TravelRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripNotesUseCase @Inject constructor(
    private val travelRepository: TravelRepository
) {
    operator fun invoke(): Flow<List<TripNote>> {
        return travelRepository.getTripNotes()
    }
}
