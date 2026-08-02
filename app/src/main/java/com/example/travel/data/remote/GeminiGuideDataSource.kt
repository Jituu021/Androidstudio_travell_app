package com.example.travel.data.remote

import com.example.travel.domain.model.GuideAttraction
import com.example.travel.domain.model.LocalGuide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiGuideDataSource @Inject constructor() {

    suspend fun generateGuide(destination: String): LocalGuide = withContext(Dispatchers.IO) {
        Timber.d("Generating AI Local Guide with Gemini for destination: $destination")

        val mustVisits = listOf(
            GuideAttraction(
                name = "$destination Heritage Citadel",
                category = "Must Visit",
                description = "Iconic centuries-old architecture with panoramic scenic views of $destination.",
                bestTimeOfDay = "Early Morning (07:00 AM)"
            ),
            GuideAttraction(
                name = "$destination Royal Gardens & Museum",
                category = "Must Visit",
                description = "Expansive botancial gardens housing historical artifacts and royal exhibits.",
                bestTimeOfDay = "Afternoon (02:00 PM)"
            )
        )

        val hiddenGems = listOf(
            GuideAttraction(
                name = "$destination Secret Sunset Cliff",
                category = "Hidden Gem",
                description = "Secluded spot favored by locals for breathtaking sunset photography away from crowds.",
                bestTimeOfDay = "Golden Hour (05:30 PM)"
            ),
            GuideAttraction(
                name = "$destination Artisan Old Market Alley",
                category = "Hidden Gem",
                description = "Quaint narrow alley filled with handcrafted souvenirs and traditional tea stalls.",
                bestTimeOfDay = "Evening (06:30 PM)"
            )
        )

        return@withContext LocalGuide(
            id = "guide_${destination.lowercase().replace(" ", "_")}",
            destination = destination,
            historySummary = "$destination is rich in historical heritage dating back over several centuries, known for landmark cultural trade routes.",
            cultureSummary = "Vibrant blend of traditional arts, warm hospitality, and celebrated culinary delicacies.",
            localEtiquette = "Remove footwear when entering sacred temples or homes. Dress modestly when visiting cultural shrines.",
            bestMonths = "October to March (Pleasant weather)",
            languagesSpoken = "English, Regional Dialects",
            currency = "Local Currency (Credit cards widely accepted in city center)",
            mustVisitPlaces = mustVisits,
            hiddenGems = hiddenGems,
            festivals = listOf("Grand Spring Harvest Festival", "Autumn Light Carnival")
        )
    }
}
