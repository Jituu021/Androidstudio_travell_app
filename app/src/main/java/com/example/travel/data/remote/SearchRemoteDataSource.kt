package com.example.travel.data.remote

import com.example.travel.gis.domain.model.MapLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRemoteDataSource @Inject constructor() {

    suspend fun searchPhoton(
        query: String,
        userLat: Double,
        userLon: Double
    ): List<MapLocation> = withContext(Dispatchers.IO) {
        val results = mutableListOf<MapLocation>()
        try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val urlString = "https://photon.komoot.io/api/?q=$encoded&lat=$userLat&lon=$userLon&limit=12"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(jsonText)
                val features = json.optJSONArray("features") ?: JSONArray()
                for (i in 0 until features.length()) {
                    val feature = features.getJSONObject(i)
                    val geometry = feature.getJSONObject("geometry")
                    val coords = geometry.getJSONArray("coordinates")
                    val lon = coords.getDouble(0)
                    val lat = coords.getDouble(1)

                    val props = feature.getJSONObject("properties")
                    val name = props.optString("name", query)
                    val street = props.optString("street", "")
                    val city = props.optString("city", props.optString("state", ""))
                    val country = props.optString("country", "")
                    val category = props.optString("osm_value", "Location").replaceFirstChar { it.uppercase() }

                    val fullAddress = listOfNotNull(
                        name.takeIf { it.isNotBlank() },
                        street.takeIf { it.isNotBlank() },
                        city.takeIf { it.isNotBlank() },
                        country.takeIf { it.isNotBlank() }
                    ).distinct().joinToString(", ")

                    results.add(
                        MapLocation(
                            id = "photon_${i}_${System.currentTimeMillis()}",
                            name = name.ifBlank { query },
                            address = fullAddress.ifEmpty { "GPS: $lat, $lon" },
                            latitude = lat,
                            longitude = lon,
                            category = category,
                            rating = 4.8f
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Photon remote search exception for query: $query")
        }
        return@withContext results
    }

    suspend fun searchNominatim(
        query: String,
        userLat: Double,
        userLon: Double
    ): List<MapLocation> = withContext(Dispatchers.IO) {
        val results = mutableListOf<MapLocation>()
        try {
            val encoded = URLEncoder.encode(query, "UTF-8")
            val urlString = "https://nominatim.openstreetmap.org/search?format=json&q=$encoded&addressdetails=1&limit=10"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().readText()
                val array = JSONArray(jsonText)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val displayName = obj.optString("display_name", query)
                    val lat = obj.optDouble("lat", userLat)
                    val lon = obj.optDouble("lon", userLon)
                    val category = obj.optString("type", "Location").replaceFirstChar { it.uppercase() }

                    val parts = displayName.split(",")
                    val mainName = parts.firstOrNull()?.trim() ?: query
                    val addressText = parts.drop(1).take(3).joinToString(", ").trim()

                    results.add(
                        MapLocation(
                            id = "nom_${i}_${System.currentTimeMillis()}",
                            name = mainName,
                            address = addressText.ifEmpty { displayName },
                            latitude = lat,
                            longitude = lon,
                            category = category,
                            rating = 4.7f
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Nominatim search exception for query: $query")
        }
        return@withContext results
    }

    suspend fun reverseGeocode(userLat: Double, userLon: Double): String = withContext(Dispatchers.IO) {
        try {
            val urlString = "https://nominatim.openstreetmap.org/reverse?format=json&lat=$userLat&lon=$userLon"
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 13)")
            conn.connectTimeout = 4000
            conn.readTimeout = 4000

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().readText()
                val json = JSONObject(jsonText)
                val displayName = json.optString("display_name", "")
                if (displayName.isNotBlank()) {
                    val parts = displayName.split(",")
                    return@withContext parts.take(4).joinToString(", ").trim()
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Nominatim reverse geocode error")
        }
        return@withContext String.format(Locale.US, "GPS: %.5f°, %.5f°", userLat, userLon)
    }
}
