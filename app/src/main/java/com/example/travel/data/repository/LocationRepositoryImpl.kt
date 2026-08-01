package com.example.travel.data.repository

import com.example.travel.core.location.LocationMode
import com.example.travel.core.location.SmartLocationEngine
import com.example.travel.domain.repository.LocationRepository
import com.example.travel.gis.domain.model.LocationTelemetry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepositoryImpl @Inject constructor(
    private val smartLocationEngine: SmartLocationEngine
) : LocationRepository {

    override fun getLocationStream(): Flow<LocationTelemetry> {
        return smartLocationEngine.getLocationStream()
    }

    override fun getTelemetryState(): StateFlow<LocationTelemetry> {
        return smartLocationEngine.telemetryState
    }

    override fun setLocationMode(mode: LocationMode) {
        smartLocationEngine.setLocationMode(mode)
    }
}
