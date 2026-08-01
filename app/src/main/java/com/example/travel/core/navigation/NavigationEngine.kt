package com.example.travel.core.navigation

import android.content.Context
import android.content.Intent
import com.example.travel.core.location.SmartLocationEngine
import com.example.travel.core.navigation.service.NavigationService
import com.example.travel.gis.domain.model.LocationTelemetry
import com.example.travel.gis.domain.model.MapRoute
import com.example.travel.gis.domain.model.RouteStep
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

data class NavigationState(
    val isNavigating: Boolean = false,
    val isPaused: Boolean = false,
    val activeRoute: MapRoute? = null,
    val currentStepIndex: Int = 0,
    val nextInstruction: String = "Proceed on route",
    val maneuverIcon: String = "⬆️",
    val currentRoadName: String = "Main Road",
    val remainingDistanceMeters: Double = 0.0,
    val remainingDurationSeconds: Double = 0.0,
    val estimatedArrivalTime: String = "--:--",
    val currentSpeedKmH: Float = 0f,
    val speedLimitKmH: Int = 60,
    val isOffRoute: Boolean = false,
    val hasArrived: Boolean = false,
    val isNightMode: Boolean = false
)

@Singleton
class NavigationEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val smartLocationEngine: SmartLocationEngine
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _navState = MutableStateFlow(NavigationState())
    val navState: StateFlow<NavigationState> = _navState.asStateFlow()

    init {
        monitorLocationForNavigation()
    }

    fun startNavigation(route: MapRoute) {
        val firstStep = route.steps.firstOrNull()
        val eta = calculateEta(route.totalDurationSeconds.toDouble())

        _navState.value = NavigationState(
            isNavigating = true,
            isPaused = false,
            activeRoute = route,
            currentStepIndex = 0,
            nextInstruction = firstStep?.instruction ?: "Head towards destination",
            maneuverIcon = getManeuverIcon(firstStep?.maneuver ?: "straight"),
            currentRoadName = firstStep?.instruction ?: "Main Road",
            remainingDistanceMeters = route.totalDistanceMeters,
            remainingDurationSeconds = route.totalDurationSeconds.toDouble(),
            estimatedArrivalTime = eta,
            hasArrived = false
        )

        startForegroundNavigationService(firstStep?.instruction ?: "Navigating...")
        Timber.d("Turn-by-turn navigation started for route: ${route.id}")
    }

    fun pauseNavigation() {
        _navState.value = _navState.value.copy(isPaused = true)
    }

    fun resumeNavigation() {
        _navState.value = _navState.value.copy(isPaused = false)
    }

    fun stopNavigation() {
        _navState.value = NavigationState(isNavigating = false)
        stopForegroundNavigationService()
        Timber.d("Navigation stopped")
    }

    fun toggleNightMode() {
        _navState.value = _navState.value.copy(isNightMode = !_navState.value.isNightMode)
    }

    private fun monitorLocationForNavigation() {
        scope.launch {
            smartLocationEngine.telemetryState.collect { telemetry ->
                if (!_navState.value.isNavigating || _navState.value.isPaused) return@collect
                val route = _navState.value.activeRoute ?: return@collect

                // Distance to destination
                val distToDest = calculateDistanceMeters(
                    telemetry.latitude, telemetry.longitude,
                    route.destination.latitude, route.destination.longitude
                )

                if (distToDest < 20.0) {
                    // Arrived at destination
                    _navState.value = _navState.value.copy(
                        hasArrived = true,
                        nextInstruction = "🎉 You have arrived at your destination!",
                        maneuverIcon = "🏁",
                        remainingDistanceMeters = 0.0,
                        remainingDurationSeconds = 0.0
                    )
                    return@collect
                }

                // Check off-route status (distance from nearest polyline point > 45m)
                val minPolylineDist = route.polylinePoints.minOfOrNull { pt ->
                    calculateDistanceMeters(telemetry.latitude, telemetry.longitude, pt.first, pt.second)
                } ?: 0.0

                val isOff = minPolylineDist > 45.0
                if (isOff != _navState.value.isOffRoute) {
                    Timber.w("Off-route status changed: $isOff (Distance from route: ${minPolylineDist.toInt()}m)")
                }

                // Find active step index based on nearest step coordinate
                var nextIdx = _navState.value.currentStepIndex
                if (route.steps.isNotEmpty() && nextIdx < route.steps.size) {
                    val currentStep = route.steps[nextIdx]
                    val distToStep = calculateDistanceMeters(
                        telemetry.latitude, telemetry.longitude,
                        currentStep.endLat, currentStep.endLon
                    )
                    if (distToStep < 25.0 && nextIdx + 1 < route.steps.size) {
                        nextIdx++
                    }
                }

                val activeStep = route.steps.getOrNull(nextIdx)
                val eta = calculateEta(distToDest / 11.0) // ~40 km/h average speed

                _navState.value = _navState.value.copy(
                    currentStepIndex = nextIdx,
                    nextInstruction = activeStep?.instruction ?: "Proceed to destination",
                    maneuverIcon = getManeuverIcon(activeStep?.maneuver ?: "straight"),
                    currentRoadName = activeStep?.instruction ?: "Route Segment",
                    remainingDistanceMeters = distToDest,
                    remainingDurationSeconds = distToDest / 11.0,
                    estimatedArrivalTime = eta,
                    currentSpeedKmH = telemetry.speedKmH,
                    isOffRoute = isOff
                )
            }
        }
    }

    private fun getManeuverIcon(maneuver: String): String {
        val lower = maneuver.lowercase()
        return when {
            lower.contains("left") -> "⬅️"
            lower.contains("right") -> "➡️"
            lower.contains("uturn") || lower.contains("u-turn") -> "↩️"
            lower.contains("roundabout") -> "🔄"
            lower.contains("arrive") || lower.contains("destination") -> "🏁"
            else -> "⬆️"
        }
    }

    private fun calculateEta(durationSeconds: Double): String {
        val etaTimeMs = System.currentTimeMillis() + (durationSeconds * 1000).toLong()
        val sdf = SimpleDateFormat("hh:mm a", Locale.US)
        return sdf.format(Date(etaTimeMs))
    }

    private fun startForegroundNavigationService(instruction: String) {
        val intent = Intent(context, NavigationService::class.java).apply {
            putExtra(NavigationService.EXTRA_NEXT_INSTRUCTION, instruction)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopForegroundNavigationService() {
        val intent = Intent(context, NavigationService::class.java).apply {
            action = NavigationService.ACTION_STOP_NAVIGATION
        }
        context.startService(intent)
    }

    private fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
