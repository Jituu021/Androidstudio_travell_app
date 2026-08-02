package com.example.travel.domain.usecase.photomemory.story

import android.content.Context
import android.content.Intent
import com.example.travel.domain.model.TravelStory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ShareTravelStoryUseCase @Inject constructor(
    @ApplicationContext private val context: Context
) {
    operator fun invoke(story: TravelStory) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, story.title)
            putExtra(Intent.EXTRA_TEXT, "Check out my Travel Story: ${story.title}\nCreated with Travel Buddy!")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Travel Story").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
