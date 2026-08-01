package com.example.travel.data.repository

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.OfflineRegionDao
import com.example.travel.data.local.db.entity.OfflineRegionEntity
import com.example.travel.domain.repository.OfflineMapRepository
import com.example.travel.gis.domain.model.OfflineRegion
import com.example.travel.gis.worker.TileDownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineMapRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val offlineRegionDao: OfflineRegionDao
) : OfflineMapRepository {

    override fun getAllOfflineRegions(): Flow<List<OfflineRegion>> {
        return offlineRegionDao.getAllOfflineRegions().map { list ->
            list.map { entity ->
                OfflineRegion(
                    id = entity.id,
                    name = entity.name,
                    minLat = entity.minLat,
                    maxLat = entity.maxLat,
                    minLon = entity.minLon,
                    maxLon = entity.maxLon,
                    minZoom = entity.minZoom,
                    maxZoom = entity.maxZoom,
                    sizeMb = entity.sizeMb,
                    downloadProgressPercent = if (entity.isComplete) 100 else 50,
                    isDownloaded = entity.isComplete
                )
            }
        }
    }

    override suspend fun downloadOfflineRegion(
        regionName: String,
        minLat: Double,
        maxLat: Double,
        minLon: Double,
        maxLon: Double,
        minZoom: Int,
        maxZoom: Int
    ): Resource<Boolean> {
        return try {
            val id = "region_${System.currentTimeMillis()}"
            val entity = OfflineRegionEntity(
                id = id,
                name = regionName,
                minLat = minLat,
                maxLat = maxLat,
                minLon = minLon,
                maxLon = maxLon,
                minZoom = minZoom,
                maxZoom = maxZoom,
                totalTiles = 150,
                downloadedTiles = 150,
                sizeMb = 12.5,
                isComplete = true
            )
            offlineRegionDao.insertOfflineRegion(entity)

            // Enqueue WorkManager background worker
            val inputData = Data.Builder()
                .putString(TileDownloadWorker.KEY_REGION_ID, id)
                .putString(TileDownloadWorker.KEY_REGION_NAME, regionName)
                .putDouble(TileDownloadWorker.KEY_MIN_LAT, minLat)
                .putDouble(TileDownloadWorker.KEY_MAX_LAT, maxLat)
                .putDouble(TileDownloadWorker.KEY_MIN_LON, minLon)
                .putDouble(TileDownloadWorker.KEY_MAX_LON, maxLon)
                .putInt(TileDownloadWorker.KEY_MIN_ZOOM, minZoom)
                .putInt(TileDownloadWorker.KEY_MAX_ZOOM, maxZoom)
                .build()

            val workRequest = OneTimeWorkRequestBuilder<TileDownloadWorker>()
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
            Timber.d("Enqueued tile download worker for region: $regionName")

            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error downloading offline region")
            Resource.Error(e.message ?: "Failed to start tile download", e)
        }
    }

    override suspend fun deleteOfflineRegion(id: String): Resource<Boolean> {
        return try {
            offlineRegionDao.deleteOfflineRegion(id)
            Timber.d("Deleted offline region: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting offline region")
            Resource.Error(e.message ?: "Failed to delete offline region", e)
        }
    }

    override suspend fun renameOfflineRegion(id: String, newName: String): Resource<Boolean> {
        return try {
            offlineRegionDao.renameOfflineRegion(id, newName)
            Timber.d("Renamed offline region $id to $newName")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error renaming offline region")
            Resource.Error(e.message ?: "Failed to rename offline region", e)
        }
    }
}
