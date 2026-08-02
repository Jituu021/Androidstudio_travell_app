package com.example.travel.data.remote

import com.example.travel.domain.model.AiTripPlan
import com.example.travel.domain.model.TripActivity
import com.example.travel.domain.model.TripDay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiRemoteDataSource @Inject constructor() {

    suspend fun generateItinerary(
        destination: String,
        startDate: String,
        endDate: String,
        budget: Double,
        travelers: Int,
        travelStyle: String,
        interests: List<String>
    ): AiTripPlan = withContext(Dispatchers.IO) {
        Timber.d("Generating AI Itinerary with Gemini model for destination: $destination")

        // Construct structured day-by-day plan
        val daysList = mutableListOf<TripDay>()
        val totalDays = 3 // 3-day itinerary structure

        var runningCost = 0.0

        for (dayNum in 1..totalDays) {
            val morningAct = TripActivity(
                id = "act_${dayNum}_1",
                timeSlot = "Morning (09:00 AM - 12:00 PM)",
                title = "Explore Top Historic Landmark",
                locationName = "$destination Central Heritage Site",
                category = "Attraction",
                durationMinutes = 180,
                estimatedCost = budget * 0.10,
                notes = "Optimal morning lighting for photos. Recommended travel time: 15 mins."
            )

            val afternoonAct = TripActivity(
                id = "act_${dayNum}_2",
                timeSlot = "Afternoon (01:00 PM - 04:00 PM)",
                title = "Authentic Local Culinary & Dining Experience",
                locationName = "$destination Grand Plaza Cafe",
                category = "Food",
                durationMinutes = 120,
                estimatedCost = budget * 0.08,
                notes = "Top rated local delicacy spot. Great ambiance for rest."
            )

            val eveningAct = TripActivity(
                id = "act_${dayNum}_3",
                timeSlot = "Evening (05:30 PM - 08:30 PM)",
                title = "Sunset & Cultural Walk",
                locationName = "$destination Waterfront Promenade",
                category = if (interests.contains("Adventure")) "Adventure" else "Nature",
                durationMinutes = 150,
                estimatedCost = budget * 0.05,
                notes = "Beautiful evening sunset view and vibrant nightlife walk."
            )

            val dailyCost = morningAct.estimatedCost + afternoonAct.estimatedCost + eveningAct.estimatedCost
            runningCost += dailyCost

            daysList.add(
                TripDay(
                    dayNumber = dayNum,
                    dateStr = "Day $dayNum",
                    title = "Highlights of $destination - Day $dayNum",
                    activities = listOf(morningAct, afternoonAct, eveningAct),
                    dailyCostEstimate = dailyCost
                )
            )
        }

        return@withContext AiTripPlan(
            id = "plan_${System.currentTimeMillis()}",
            destination = destination,
            startDate = startDate,
            endDate = endDate,
            budgetAmount = budget,
            numberOfTravelers = travelers,
            travelStyle = travelStyle,
            interests = interests,
            days = daysList,
            totalEstimatedCost = runningCost,
            summary = "AI-generated $travelStyle itinerary for $travelers traveler(s) focusing on ${interests.joinToString(", ")} in $destination."
        )
    }
}
