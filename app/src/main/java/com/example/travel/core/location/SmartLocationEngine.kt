package com.example.travel.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Looper
import com.example.travel.data.local.datastore.UserPreferencesDataStore
import com.example.travel.gis.domain.model.LocationTelemetry
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

enum class LocationMode {
    HIGH_ACCURACY, BALANCED, BATTERY_SAVER
}

@Singleton
class SmartLocationEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesDataStore: UserPreferencesDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val _currentMode = MutableStateFlow(LocationMode.HIGH_ACCURACY)
    val currentMode: StateFlow<LocationMode> = _currentMode.asStateFlow()

    private val _telemetryState = MutableStateFlow(
        LocationTelemetry(
            latitude = 34.1526,
            longitude = 77.5771,
            accuracyMeters = 5f,
            speedKmH = 0f,
            bearingDegrees = 0f,
            altitudeMeters = 3500.0,
            totalDistanceMeters = 0.0,
            activityType = "Stationary",
            isMockLocationDetected = false,
            isGpsSignalLost = false
        )
    )
    val telemetryState: StateFlow<LocationTelemetry> = _telemetryState.asStateFlow()

    private var currentHeading: Float = 0f
    private var lastLocation: Location? = null
    private var accumulatedDistance: Double = 0.0

    // Low-pass filter weights
    private var filteredLat: Double = 0.0
    private var filteredLon: Double = 0.0
    private val alpha = 0.35 // Smoothing factor

    init {
        setupCompassListener()
        monitorGpsSignalTimeout()
    }

    fun setLocationMode(mode: LocationMode) {
        _currentMode.value = mode
        Timber.d("Location engine mode changed to: $mode")
    }

    private fun setupCompassListener() {
        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor != null) {
            sensorManager.registerListener(object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_ROTATION_VECTOR) {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                        currentHeading = (azimuth + 360) % 360
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun monitorGpsSignalTimeout() {
        scope.launch {
            while (true) {
                delay(10000) // Check every 10s
                val lastTime = _telemetryState.value.timestamp
                val elapsed = System.currentTimeMillis() - lastTime
                if (elapsed > 12000) { // 12 seconds without GPS fix
                    if (!_telemetryState.value.isGpsSignalLost) {
                        Timber.w("GPS Signal loss detected! Elapsed: ${elapsed}ms")
                        _telemetryState.value = _telemetryState.value.copy(isGpsSignalLost = true)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationStream(): Flow<LocationTelemetry> = callbackFlow {
        val priority = when (_currentMode.value) {
            LocationMode.HIGH_ACCURACY -> Priority.PRIORITY_HIGH_ACCURACY
            LocationMode.BALANCED -> Priority.PRIORITY_BALANCED_POWER_ACCURACY
            LocationMode.BATTERY_SAVER -> Priority.PRIORITY_LOW_POWER
        }

        val locationRequest = LocationRequest.Builder(priority, 1000)
            .setMinUpdateIntervalMillis(500)
            .setMinUpdateDistanceMeters(0.5f)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    val telemetry = processRawLocation(location)
                    _telemetryState.value = telemetry
                    trySend(telemetry)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
        } catch (e: Exception) {
            Timber.e(e, "Error requesting location updates")
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    private fun processRawLocation(location: Location): LocationTelemetry {
        val isMock = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            location.isMock
        } else {
            @Suppress("DEPRECATION")
            location.isFromMockProvider
        }

        // Apply low-pass exponential moving average filter
        if (filteredLat == 0.0 && filteredLon == 0.0) {
            filteredLat = location.latitude
            filteredLon = location.longitude
        } else {
            filteredLat += alpha * (location.latitude - filteredLat)
            filteredLon += alpha * (location.longitude - filteredLon)
        }

        // Distance delta
        var deltaMeters = 0.0
        lastLocation?.let { prev ->
            deltaMeters = calculateHaversineDistance(
                prev.latitude, prev.longitude,
                filteredLat, filteredLon
            )
            if (deltaMeters > 0.5 && deltaMeters < 500.0) { // Filter unrealistic jumps
                accumulatedDistance += deltaMeters
            }
        }
        lastLocation = location

        val speed = if (location.hasSpeed()) location.speed * 3.6f else 0f
        val activity = when {
            speed < 1.0f -> "Stationary"
            speed in 1.0f..15.0f -> "Walking"
            speed in 15.0f..30.0f -> "Cycling"
            else -> "Driving"
        }

        val bearing = if (location.hasBearing()) location.bearing else currentHeading

        return LocationTelemetry(
            latitude = filteredLat,
            longitude = filteredLon,
            accuracyMeters = location.accuracy,
            speedKmH = speed,
            bearingDegrees = bearing,
            altitudeMeters = location.altitude,
            totalDistanceMeters = accumulatedDistance,
            activityType = activity,
            isMockLocationDetected = isMock,
            isGpsSignalLost = false,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
