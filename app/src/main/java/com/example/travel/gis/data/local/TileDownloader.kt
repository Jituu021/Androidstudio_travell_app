package com.example.travel.gis.data.local

import android.content.Context
import com.example.travel.gis.domain.model.OfflineRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TileDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val tilesDir = File(context.filesDir, "offline_tiles").apply { mkdirs() }

    private val _downloadProgress = MutableStateFlow<Pair<String, Int>>("" to 0)
    val downloadProgress: StateFlow<Pair<String, Int>> = _downloadProgress

    suspend fun downloadRegion(region: OfflineRegion): Boolean = withContext(Dispatchers.IO) {
        try {
            val regionDir = File(tilesDir, region.id).apply { mkdirs() }
            val minZ = region.minZoom
            val maxZ = region.maxZoom.coerceAtMost(16) // Reasonable max zoom limit

            var totalTiles = 0
            val tileTasks = mutableListOf<Triple<Int, Int, Int>>() // z, x, y

            for (z in minZ..maxZ) {
                val minX = lonToTileX(region.minLon, z)
                val maxX = lonToTileX(region.maxLon, z)
                val minY = latToTileY(region.maxLat, z) // Y axis inverted in tiles
                val maxY = latToTileY(region.minLat, z)

                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        tileTasks.add(Triple(z, x, y))
                        totalTiles++
                    }
                }
            }

            if (totalTiles == 0) return@withContext false

            var downloadedCount = 0
            for (task in tileTasks) {
                val (z, x, y) = task
                val tileFile = File(regionDir, "$z-$x-$y.png")
                if (!tileFile.exists()) {
                    val tileUrl = "https://a.basemaps.cartocdn.com/rastertiles/voyager/$z/$x/$y.png"
                    downloadSingleTile(tileUrl, tileFile)
                }
                downloadedCount++
                val percent = ((downloadedCount.toDouble() / totalTiles) * 100).toInt()
                _downloadProgress.value = region.id to percent
            }

            return@withContext true
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext false
        }
    }

    private fun downloadSingleTile(urlString: String, outputFile: File) {
        try {
            val url = URL(urlString)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 3000
            conn.readTimeout = 3000
            if (conn.responseCode == 200) {
                conn.inputStream.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore single tile fetch failures
        }
    }

    fun deleteRegionStorage(regionId: String): Boolean {
        val dir = File(tilesDir, regionId)
        return if (dir.exists()) dir.deleteRecursively() else true
    }

    private fun lonToTileX(lon: Double, zoom: Int): Int {
        return floor((lon + 180.0) / 360.0 * (1 shl zoom)).toInt()
    }

    private fun latToTileY(lat: Double, zoom: Int): Int {
        val latRad = Math.toRadians(lat)
        return floor((1.0 - ln(tan(latRad) + 1.0 / cos(latRad)) / Math.PI) / 2.0 * (1 shl zoom)).toInt()
    }
}
