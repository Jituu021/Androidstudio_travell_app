package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.PackingDao
import com.example.travel.data.local.db.entity.PackingItemEntity
import com.example.travel.domain.model.PackingItem
import com.example.travel.domain.repository.PackingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PackingRepositoryImpl @Inject constructor(
    private val packingDao: PackingDao
) : PackingRepository {

    override fun getPackingItems(tripId: String): Flow<List<PackingItem>> {
        return packingDao.getPackingItemsForTrip(tripId).map { list ->
            list.map { e ->
                PackingItem(
                    id = e.id,
                    tripId = e.tripId,
                    category = e.category,
                    itemName = e.itemName,
                    isPacked = e.isPacked,
                    isEssential = e.isEssential,
                    timestamp = e.timestamp
                )
            }
        }
    }

    override suspend fun generatePackingList(
        destination: String,
        travelType: String,
        durationDays: Int,
        weatherCondition: String
    ): Resource<List<PackingItem>> {
        return try {
            val tripId = "trip_${destination.lowercase().replace(" ", "_")}"
            val items = mutableListOf<PackingItem>()

            // Essential Documents
            items.add(PackingItem(id = "${tripId}_doc_1", tripId = tripId, category = "Documents", itemName = "Passport / National ID", isEssential = true))
            items.add(PackingItem(id = "${tripId}_doc_2", tripId = tripId, category = "Documents", itemName = "Travel Insurance & Booking Tickets", isEssential = true))
            items.add(PackingItem(id = "${tripId}_doc_3", tripId = tripId, category = "Documents", itemName = "Credit / Debit Cards & Emergency Cash", isEssential = true))

            // Electronics
            items.add(PackingItem(id = "${tripId}_elec_1", tripId = tripId, category = "Electronics", itemName = "Smartphone & Charger Cable", isEssential = true))
            items.add(PackingItem(id = "${tripId}_elec_2", tripId = tripId, category = "Electronics", itemName = "10,000mAh Power Bank", isEssential = true))
            items.add(PackingItem(id = "${tripId}_elec_3", tripId = tripId, category = "Electronics", itemName = "Universal Plug Adapter"))

            // Clothing based on Weather & Duration
            val shirtsCount = (durationDays + 1).coerceAtMost(7)
            items.add(PackingItem(id = "${tripId}_cloth_1", tripId = tripId, category = "Clothing", itemName = "$shirtsCount x Comfortable Shirts/T-shirts", isEssential = true))
            items.add(PackingItem(id = "${tripId}_cloth_2", tripId = tripId, category = "Clothing", itemName = "2 x Pairs of Pants/Shorts"))

            if (weatherCondition.contains("Rain", ignoreCase = true) || weatherCondition.contains("Drizzle", ignoreCase = true)) {
                items.add(PackingItem(id = "${tripId}_cloth_rain", tripId = tripId, category = "Clothing", itemName = "Waterproof Raincoat / Compact Umbrella", isEssential = true))
            } else if (weatherCondition.contains("Snow", ignoreCase = true) || weatherCondition.contains("Cold", ignoreCase = true)) {
                items.add(PackingItem(id = "${tripId}_cloth_cold", tripId = tripId, category = "Clothing", itemName = "Thermal Jacket & Gloves", isEssential = true))
            } else {
                items.add(PackingItem(id = "${tripId}_cloth_sun", tripId = tripId, category = "Clothing", itemName = "UV Protection Sunglasses & Sun Hat"))
            }

            // Toiletries
            items.add(PackingItem(id = "${tripId}_toil_1", tripId = tripId, category = "Toiletries", itemName = "Toothbrush, Toothpaste & Floss", isEssential = true))
            items.add(PackingItem(id = "${tripId}_toil_2", tripId = tripId, category = "Toiletries", itemName = "Travel Shampoo & Body Wash"))
            items.add(PackingItem(id = "${tripId}_toil_3", tripId = tripId, category = "Toiletries", itemName = "SPF 50+ Sunscreen Lotion"))

            // Medicines & Emergency Kit
            items.add(PackingItem(id = "${tripId}_med_1", tripId = tripId, category = "Medicines", itemName = "Personal Prescription Medicines", isEssential = true))
            items.add(PackingItem(id = "${tripId}_med_2", tripId = tripId, category = "Emergency Kit", itemName = "First Aid Bandages & Antiseptic Wipes", isEssential = true))
            items.add(PackingItem(id = "${tripId}_med_3", tripId = tripId, category = "Medicines", itemName = "Motion Sickness & ORS Packets"))

            // Travel Type Extras
            when (travelType) {
                "Beach" -> {
                    items.add(PackingItem(id = "${tripId}_beach_1", tripId = tripId, category = "Accessories", itemName = "Swimwear & Quick-Dry Towel"))
                    items.add(PackingItem(id = "${tripId}_beach_2", tripId = tripId, category = "Accessories", itemName = "Waterproof Phone Pouch"))
                }
                "Camping", "Adventure" -> {
                    items.add(PackingItem(id = "${tripId}_adv_1", tripId = tripId, category = "Accessories", itemName = "Trekking Shoes & Headlamp Flashlight", isEssential = true))
                    items.add(PackingItem(id = "${tripId}_adv_2", tripId = tripId, category = "Accessories", itemName = "Multi-tool & Bug Spray"))
                }
            }

            // Save to Room DB
            val entities = items.map { i ->
                PackingItemEntity(
                    id = i.id,
                    tripId = i.tripId,
                    category = i.category,
                    itemName = i.itemName,
                    isPacked = i.isPacked,
                    isEssential = i.isEssential
                )
            }
            packingDao.insertAllPackingItems(entities)
            Timber.d("Generated & saved ${items.size} packing items for $destination")
            Resource.Success(items)
        } catch (e: Exception) {
            Timber.e(e, "Error generating packing list")
            Resource.Error(e.message ?: "Failed to generate packing list", e)
        }
    }

    override suspend fun savePackingItem(item: PackingItem): Resource<Boolean> {
        return try {
            val entity = PackingItemEntity(
                id = item.id.ifEmpty { "item_${System.currentTimeMillis()}" },
                tripId = item.tripId,
                category = item.category,
                itemName = item.itemName,
                isPacked = item.isPacked,
                isEssential = item.isEssential
            )
            packingDao.insertPackingItem(entity)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save packing item", e)
        }
    }

    override suspend fun updatePackedStatus(id: String, isPacked: Boolean): Resource<Boolean> {
        return try {
            packingDao.updatePackedStatus(id, isPacked)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update item status", e)
        }
    }

    override suspend fun deletePackingItem(id: String): Resource<Boolean> {
        return try {
            packingDao.deletePackingItem(id)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to delete item", e)
        }
    }

    override suspend fun resetPackingList(tripId: String): Resource<Boolean> {
        return try {
            packingDao.clearTripPackingList(tripId)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to reset list", e)
        }
    }
}
