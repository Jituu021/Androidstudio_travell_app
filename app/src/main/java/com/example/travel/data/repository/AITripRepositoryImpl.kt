package com.example.travel.data.repository

import com.example.travel.core.common.result.Resource
import com.example.travel.data.local.db.dao.AiTripPlanDao
import com.example.travel.data.local.db.entity.AiTripPlanEntity
import com.example.travel.data.remote.GeminiRemoteDataSource
import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.model.TripActivity
import com.example.travel.domain.model.TripDay
import com.example.travel.domain.repository.AITripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AITripRepositoryImpl @Inject constructor(
    private val geminiRemoteDataSource: GeminiRemoteDataSource,
    private val aiTripPlanDao: AiTripPlanDao
) : AITripRepository {

    override suspend fun generateTripPlan(
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        travelers: Int,
        travelStyle: String,
        interests: List<String>
    ): Resource<AiTripPlan> {
        return try {
            val plan = geminiRemoteDataSource.generateItinerary(
                destination, startDate, endDate, budget, travelers, travelStyle, interests
            )
            Resource.Success(plan)
        } catch (e: Exception) {
            Timber.e(e, "Error generating AI trip plan")
            Resource.Error(e.message ?: "Failed to generate AI trip plan", e)
        }
    }

    override fun getAllSavedTripPlans(): Flow<List<AiTripPlan>> {
        return aiTripPlanDao.getAllTripPlans().map { entities ->
            entities.map { entity ->
                parseEntityToPlan(entity)
            }
        }
    }

    override suspend fun saveTripPlan(plan: AiTripPlan): Resource<Boolean> {
        return try {
            val entity = parsePlanToEntity(plan)
            aiTripPlanDao.insertTripPlan(entity)
            Timber.d("Saved AI Trip Plan: ${plan.id} for ${plan.destination}")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error saving AI trip plan")
            Resource.Error(e.message ?: "Failed to save trip plan", e)
        }
    }

    override suspend fun deleteTripPlan(id: String): Resource<Boolean> {
        return try {
            aiTripPlanDao.deleteTripPlan(id)
            Timber.d("Deleted AI Trip Plan: $id")
            Resource.Success(true)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting AI trip plan")
            Resource.Error(e.message ?: "Failed to delete trip plan", e)
        }
    }

    private fun parsePlanToEntity(plan: AiTripPlan): AiTripPlanEntity {
        val daysArray = JSONArray()
        plan.days.forEach { day ->
            val dayObj = JSONObject().apply {
                put("dayNumber", day.dayNumber)
                put("dateStr", day.dateStr)
                put("title", day.title)
                put("dailyCostEstimate", day.dailyCostEstimate)
                val actArr = JSONArray()
                day.activities.forEach { act ->
                    actArr.put(JSONObject().apply {
                        put("id", act.id)
                        put("timeSlot", act.timeSlot)
                        put("title", act.title)
                        put("locationName", act.locationName)
                        put("category", act.category)
                        put("durationMinutes", act.durationMinutes)
                        put("estimatedCost", act.estimatedCost)
                        put("notes", act.notes)
                    })
                }
                put("activities", actArr)
            }
            daysArray.put(dayObj)
        }

        return AiTripPlanEntity(
            id = plan.id,
            destination = plan.destination,
            startDate = plan.startDate,
            endDate = plan.endDate,
            budgetAmount = plan.budgetAmount,
            numberOfTravelers = plan.numberOfTravelers,
            travelStyle = plan.travelStyle,
            interestsJson = JSONArray(plan.interests).toString(),
            itineraryDaysJson = daysArray.toString(),
            totalEstimatedCost = plan.totalEstimatedCost,
            summary = plan.summary
        )
    }

    private fun parseEntityToPlan(entity: AiTripPlanEntity): AiTripPlan {
        val interestsList = mutableListOf<String>()
        try {
            val iArr = JSONArray(entity.interestsJson)
            for (i in 0 until iArr.length()) {
                interestsList.add(iArr.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val daysList = mutableListOf<TripDay>()
        try {
            val dArr = JSONArray(entity.itineraryDaysJson)
            for (i in 0 until dArr.length()) {
                val dObj = dArr.getJSONObject(i)
                val dayNum = dObj.getInt("dayNumber")
                val dateStr = dObj.getString("dateStr")
                val title = dObj.getString("title")
                val dailyCost = dObj.getDouble("dailyCostEstimate")

                val actList = mutableListOf<TripActivity>()
                val aArr = dObj.getJSONArray("activities")
                for (j in 0 until aArr.length()) {
                    val aObj = aArr.getJSONObject(j)
                    actList.add(
                        TripActivity(
                            id = aObj.optString("id", "act_$j"),
                            timeSlot = aObj.optString("timeSlot", "Morning"),
                            title = aObj.optString("title", "Activity"),
                            locationName = aObj.optString("locationName", entity.destination),
                            category = aObj.optString("category", "Attraction"),
                            durationMinutes = aObj.optInt("durationMinutes", 90),
                            estimatedCost = aObj.optDouble("estimatedCost", 0.0),
                            notes = aObj.optString("notes", "")
                        )
                    )
                }
                daysList.add(TripDay(dayNum, dateStr, title, actList, dailyCost))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return AiTripPlan(
            id = entity.id,
            destination = entity.destination,
            startDate = entity.startDate,
            endDate = entity.endDate,
            budgetAmount = entity.budgetAmount,
            numberOfTravelers = entity.numberOfTravelers,
            travelStyle = entity.travelStyle,
            interests = interestsList,
            days = daysList,
            totalEstimatedCost = entity.totalEstimatedCost,
            summary = entity.summary,
            timestamp = entity.timestamp
        )
    }
}
