package com.example.travel.data.remote

import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NearbyRemoteDataSource @Inject constructor() {

    suspend fun fetchNearbyOverpass(
        category: String,
        userLat: Double,
        userLon: Double,
        radiusMeters: Int = 3000
    ): List<MapLocation> = withContext(Dispatchers.IO) {
        val results = mutableListOf<MapLocation>()
        try {
            val tag = mapCategoryToOsmTag(category)
            val overpassQuery = """
                [out:json][timeout:10];
                (
                  node[$tag](around:$radiusMeters,$userLat,$userLon);
                );
                out body 20;
            """.trimIndent()

            val encodedQuery = URLEncoder.encode(overpassQuery, "UTF-8")
            val url = URL("https://overpass-api.de/api/interpreter?data=$encodedQuery")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(responseText)
                val elements = json.optJSONArray("elements") ?: return@withContext emptyList()

                for (i in 0 until elements.length()) {
                    val elem = elements.getJSONObject(i)
                    val lat = elem.getDouble("lat")
                    val lon = elem.getDouble("lon")
                    val tags = elem.optJSONObject("tags")
                    val name = tags?.optString("name", "$category Point #${i + 1}") ?: "$category Point #${i + 1}"
                    val street = tags?.optString("addr:street", "") ?: ""
                    val city = tags?.optString("addr:city", "") ?: ""

                    val address = listOf(street, city).filter { it.isNotBlank() }.joinToString(", ")
                        .ifEmpty { "Near $userLat, $userLon" }

                    results.add(
                        MapLocation(
                            id = "overpass_${elem.optLong("id", i.toLong())}",
                            name = name,
                            address = address,
                            latitude = lat,
                            longitude = lon,
                            category = category,
                            rating = 4.8f - (i * 0.1f).coerceAtMost(0.5f)
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Overpass API exception for category: $category")
        }
        return@withContext results
    }

    private fun mapCategoryToOsmTag(category: String): String {
        return when (category) {
            "ATM", "ATMs" -> "\"amenity\"=\"atm\""
            "Pharmacy", "Pharmacies" -> "\"amenity\"=\"pharmacy\""
            "Petrol Pump", "Petrol Pumps" -> "\"amenity\"=\"fuel\""
            "Toilet", "Public Toilet" -> "\"amenity\"=\"toilets\""
            "Hotel", "Hotels" -> "\"tourism\"=\"hotel\""
            "Restaurant", "Restaurants" -> "\"amenity\"=\"restaurant\""
            "Cafe", "Cafes" -> "\"amenity\"=\"cafe\""
            "Hospital", "Hospitals" -> "\"amenity\"=\"hospital\""
            "Police", "Police Stations" -> "\"amenity\"=\"police\""
            "EV Charger" -> "\"amenity\"=\"charging_station\""
            "Bus Stop", "Bus Stops" -> "\"highway\"=\"bus_stop\""
            "Railway", "Railway Stations" -> "\"railway\"=\"station\""
            "Airport", "Airports" -> "\"aeroway\"=\"aerodrome\""
            "Parking" -> "\"amenity\"=\"parking\""
            "Tourist Attraction" -> "\"tourism\"=\"attraction\""
            else -> "\"amenity\""
        }
    }
}
