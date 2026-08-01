package com.example.travel.domain.repository

import com.example.travel.core.location.LocationMode
import com.example.travel.gis.domain.model.LocationTelemetry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    fun getLocationStream(): Flow<LocationTelemetry>
    fun getTelemetryState(): StateFlow<LocationTelemetry>
    fun setLocationMode(mode: LocationMode)
}
