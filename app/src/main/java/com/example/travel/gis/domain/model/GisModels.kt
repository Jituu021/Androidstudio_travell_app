package com.example.travel.gis.domain.model

data class MapLocation(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "General",
    val rating: Float = 4.5f,
    val isFavorite: Boolean = false,
    val distanceMeters: Double = 0.0,
    val openingHours: String = "Open 24/7",
    val phone: String = "+91 1800 22 4433",
    val website: String = "https://openstreetmap.org"
)

data class RouteStep(
    val instruction: String,
    val distanceText: String,
    val distanceMeters: Double,
    val durationText: String,
    val maneuver: String, // e.g. "turn-right", "turn-left", "straight"
    val startLat: Double,
    val startLon: Double,
    val endLat: Double,
    val endLon: Double
)

data class MapRoute(
    val id: String,
    val origin: MapLocation,
    val destination: MapLocation,
    val totalDistanceText: String,
    val totalDistanceMeters: Double,
    val totalDurationText: String,
    val totalDurationSeconds: Long,
    val polylinePoints: List<Pair<Double, Double>>,
    val steps: List<RouteStep>,
    val travelMode: TravelMode = TravelMode.DRIVING
)

enum class TravelMode {
    DRIVING, WALKING, CYCLING, TRANSIT
}

data class OfflineRegion(
    val id: String,
    val name: String,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
    val minZoom: Int = 10,
    val maxZoom: Int = 17,
    val sizeMb: Double = 0.0,
    val downloadProgressPercent: Int = 0,
    val isDownloaded: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

enum class MapStyleMode {
    STREET_CARTO, SATELLITE_HYBRID, OPENSTREETMAP, TERRAIN, DARK
}

data class LocationTelemetry(
    val latitude: Double,
    val longitude: Double,
    val accuracyMeters: Float,
    val speedKmH: Float,
    val bearingDegrees: Float,
    val altitudeMeters: Double,
    val totalDistanceMeters: Double = 0.0,
    val activityType: String = "Stationary",
    val isMockLocationDetected: Boolean = false,
    val isGpsSignalLost: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
