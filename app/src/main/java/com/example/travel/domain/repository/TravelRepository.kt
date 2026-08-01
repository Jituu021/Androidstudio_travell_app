package com.example.travel.domain.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.Expense
import com.example.travel.domain.model.TripNote
import kotlinx.coroutines.flow.Flow

interface TravelRepository {
    fun getTripNotes(): Flow<List<TripNote>>
    suspend fun addTripNote(note: TripNote): Resource<Long>
    suspend fun deleteTripNote(id: Long): Resource<Unit>

    fun getExpenses(): Flow<List<Expense>>
    suspend fun addExpense(expense: Expense): Resource<Long>
    suspend fun deleteExpense(id: Long): Resource<Unit>

    fun calculateSunriseSunset(lat: Double, lon: Double, dateMillis: Long): Pair<String, String>
}
