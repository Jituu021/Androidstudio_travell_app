package com.example.travel.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travel.data.local.db.entity.PackingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PackingDao {
    @Query("SELECT * FROM packing_items WHERE tripId = :tripId ORDER BY category ASC, itemName ASC")
    fun getPackingItemsForTrip(tripId: String): Flow<List<PackingItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPackingItem(item: PackingItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPackingItems(items: List<PackingItemEntity>)

    @Query("UPDATE packing_items SET isPacked = :isPacked WHERE id = :id")
    suspend fun updatePackedStatus(id: String, isPacked: Boolean)

    @Query("DELETE FROM packing_items WHERE id = :id")
    suspend fun deletePackingItem(id: String)

    @Query("DELETE FROM packing_items WHERE tripId = :tripId")
    suspend fun clearTripPackingList(tripId: String)
}
