package com.example.travel.gis.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.travel.gis.data.local.TileDownloader
import com.example.travel.gis.domain.model.OfflineRegion
import timber.log.Timber

class TileDownloadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val regionId = inputData.getString(KEY_REGION_ID) ?: "region_${System.currentTimeMillis()}"
        val regionName = inputData.getString(KEY_REGION_NAME) ?: "Offline Region"
        val minLat = inputData.getDouble(KEY_MIN_LAT, 34.0)
        val maxLat = inputData.getDouble(KEY_MAX_LAT, 34.2)
        val minLon = inputData.getDouble(KEY_MIN_LON, 77.4)
        val maxLon = inputData.getDouble(KEY_MAX_LON, 77.6)
        val minZoom = inputData.getInt(KEY_MIN_ZOOM, 10)
        val maxZoom = inputData.getInt(KEY_MAX_ZOOM, 14)

        Timber.d("TileDownloadWorker starting for region: $regionName")
        showNotification(regionName, "Downloading map tiles...")

        return try {
            val region = OfflineRegion(
                id = regionId,
                name = regionName,
                minLat = minLat,
                maxLat = maxLat,
                minLon = minLon,
                maxLon = maxLon,
                minZoom = minZoom,
                maxZoom = maxZoom
            )
            val downloader = TileDownloader(applicationContext)
            downloader.downloadRegion(region)
            showNotification(regionName, "✅ Download complete!")
            Result.success()
        } catch (e: Exception) {
            Timber.e(e, "TileDownloadWorker failed for region: $regionName")
            showNotification(regionName, "❌ Download failed.")
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "offline_tiles_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Offline Map Downloads", NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("🗺 Offline Map: $title")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 2001
        const val KEY_REGION_ID = "key_region_id"
        const val KEY_REGION_NAME = "key_region_name"
        const val KEY_MIN_LAT = "key_min_lat"
        const val KEY_MAX_LAT = "key_max_lat"
        const val KEY_MIN_LON = "key_min_lon"
        const val KEY_MAX_LON = "key_max_lon"
        const val KEY_MIN_ZOOM = "key_min_zoom"
        const val KEY_MAX_ZOOM = "key_max_zoom"
    }
}
