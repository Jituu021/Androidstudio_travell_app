package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.LocalGuideDao
import com.example.travel.data.local.db.entity.LocalGuideEntity
import com.example.travel.data.remote.GeminiGuideDataSource
import com.example.travel.domain.model.GuideAttraction
import com.example.travel.domain.model.LocalGuide
import com.example.travel.domain.repository.LocalGuideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalGuideRepositoryImpl @Inject constructor(
    private val geminiGuideDataSource: GeminiGuideDataSource,
    private val localGuideDao: LocalGuideDao
) : LocalGuideRepository {

    override fun getAllSavedGuides(): Flow<List<LocalGuide>> {
        return localGuideDao.getAllGuides().map { list ->
            list.map { entity -> parseEntityToGuide(entity) }
        }
    }

    override suspend fun generateLocalGuide(destination: String): Resource<LocalGuide> {
        return try {
            val cached = localGuideDao.getGuideForDestination(destination)
            if (cached != null) {
                return Resource.Success(parseEntityToGuide(cached))
            }

            val guide = geminiGuideDataSource.generateGuide(destination)
            val entity = parseGuideToEntity(guide)
            localGuideDao.insertGuide(entity)
            Timber.d("Generated & cached local guide for $destination")
            Resource.Success(guide)
        } catch (e: Exception) {
            Timber.e(e, "Error generating local guide")
            Resource.Error(e.message ?: "Failed to generate local guide", e)
        }
    }

    override suspend fun saveGuide(guide: LocalGuide): Resource<Boolean> {
        return try {
            val entity = parseGuideToEntity(guide)
            localGuideDao.insertGuide(entity)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to save local guide", e)
        }
    }

    override suspend fun updateBookmarkStatus(id: String, isBookmarked: Boolean): Resource<Boolean> {
        return try {
            localGuideDao.updateBookmarkStatus(id, isBookmarked)
            Resource.Success(true)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update bookmark", e)
        }
    }

    private fun parseGuideToEntity(guide: LocalGuide): LocalGuideEntity {
        val mustVisitArr = JSONArray()
        guide.mustVisitPlaces.forEach { p ->
            mustVisitArr.put(JSONObject().apply {
                put("name", p.name)
                put("category", p.category)
                put("description", p.description)
                put("bestTimeOfDay", p.bestTimeOfDay)
            })
        }

        val hiddenArr = JSONArray()
        guide.hiddenGems.forEach { p ->
            hiddenArr.put(JSONObject().apply {
                put("name", p.name)
                put("category", p.category)
                put("description", p.description)
                put("bestTimeOfDay", p.bestTimeOfDay)
            })
        }

        return LocalGuideEntity(
            id = guide.id.ifEmpty { "guide_${guide.destination.lowercase().replace(" ", "_")}" },
            destination = guide.destination,
            historySummary = guide.historySummary,
            cultureSummary = guide.cultureSummary,
            localEtiquette = guide.localEtiquette,
            bestMonths = guide.bestMonths,
            languagesSpoken = guide.languagesSpoken,
            currency = guide.currency,
            mustVisitJson = mustVisitArr.toString(),
            hiddenGemsJson = hiddenArr.toString(),
            festivalsJson = JSONArray(guide.festivals).toString(),
            isBookmarked = guide.isBookmarked
        )
    }

    private fun parseEntityToGuide(entity: LocalGuideEntity): LocalGuide {
        val mustVisits = mutableListOf<GuideAttraction>()
        try {
            val arr = JSONArray(entity.mustVisitJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                mustVisits.add(
                    GuideAttraction(
                        name = obj.getString("name"),
                        category = obj.getString("category"),
                        description = obj.getString("description"),
                        bestTimeOfDay = obj.optString("bestTimeOfDay", "Morning")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val hiddenGems = mutableListOf<GuideAttraction>()
        try {
            val arr = JSONArray(entity.hiddenGemsJson)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                hiddenGems.add(
                    GuideAttraction(
                        name = obj.getString("name"),
                        category = obj.getString("category"),
                        description = obj.getString("description"),
                        bestTimeOfDay = obj.optString("bestTimeOfDay", "Golden Hour")
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val festivals = mutableListOf<String>()
        try {
            val arr = JSONArray(entity.festivalsJson)
            for (i in 0 until arr.length()) festivals.add(arr.getString(i))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return LocalGuide(
            id = entity.id,
            destination = entity.destination,
            historySummary = entity.historySummary,
            cultureSummary = entity.cultureSummary,
            localEtiquette = entity.localEtiquette,
            bestMonths = entity.bestMonths,
            languagesSpoken = entity.languagesSpoken,
            currency = entity.currency,
            mustVisitPlaces = mustVisits,
            hiddenGems = hiddenGems,
            festivals = festivals,
            isBookmarked = entity.isBookmarked,
            timestamp = entity.timestamp
        )
    }
}
