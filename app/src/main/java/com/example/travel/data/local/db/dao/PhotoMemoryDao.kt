package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.PhotoMemoryEntity
import com.example.travel.data.local.db.entity.PhotoMetadataEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoMemoryDao {
    @Query("SELECT * FROM photo_memories WHERE tripId = :tripId ORDER BY captureTimestamp DESC")
    fun getPhotosForTrip(tripId: String): Flow<List<PhotoMemoryEntity>>

    @Query("SELECT * FROM photo_memories ORDER BY captureTimestamp DESC")
    fun getAllPhotos(): Flow<List<PhotoMemoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPhoto(photo: PhotoMemoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetadata(metadata: PhotoMetadataEntity)

    @Query("SELECT * FROM photo_metadata WHERE photoId = :photoId LIMIT 1")
    suspend fun getMetadataForPhoto(photoId: String): PhotoMetadataEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM photo_memories WHERE fileHash = :hash)")
    suspend fun isPhotoExists(hash: String): Boolean

    @Query("SELECT * FROM ai_photo_analyses WHERE photoId = :photoId LIMIT 1")
    fun getAIAnalysis(photoId: String): Flow<com.example.travel.data.local.db.entity.AIPhotoAnalysisEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAIAnalysis(analysis: com.example.travel.data.local.db.entity.AIPhotoAnalysisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<com.example.travel.data.local.db.entity.PhotoTagEntity>)

    @Query("SELECT * FROM photo_tags WHERE photoId = :photoId")
    fun getTagsForPhoto(photoId: String): Flow<List<com.example.travel.data.local.db.entity.PhotoTagEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoment(moment: com.example.travel.data.local.db.entity.PhotoMomentEntity)

    @Query("SELECT * FROM photo_moments WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getMomentsForTrip(tripId: String): Flow<List<com.example.travel.data.local.db.entity.PhotoMomentEntity>>
}
