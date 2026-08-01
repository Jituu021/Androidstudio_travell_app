package com.example.travel.gis.provider.location

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Looper
import com.example.travel.gis.domain.model.LocationTelemetry
import com.example.travel.gis.provider.ILocationService
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FusedLocationServiceImpl(private val context: Context) : ILocationService {

    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private var currentHeading: Float = 0f
    private var lastTelemetry: LocationTelemetry? = null

    init {
        setupCompassListener()
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

    @SuppressLint("MissingPermission")
    override val locationUpdates: Flow<LocationTelemetry> = callbackFlow {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setMinUpdateIntervalMillis(500)
            .setMinUpdateDistanceMeters(0f)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                for (location in result.locations) {
                    val telemetry = LocationTelemetry(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracyMeters = location.accuracy,
                        speedKmH = (location.speed * 3.6f),
                        bearingDegrees = if (location.hasBearing()) location.bearing else currentHeading,
                        altitudeMeters = location.altitude
                    )
                    lastTelemetry = telemetry
                    trySend(telemetry)
                }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun startTrackingHighAccuracy() {
        // High accuracy tracking triggered by locationUpdates Flow
    }

    override fun stopTracking() {
        // Unregister listeners on scope cancellation
    }

    override fun getLastKnownLocation(): LocationTelemetry? {
        return lastTelemetry
    }
}
