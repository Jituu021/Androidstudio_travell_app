package com.example.travel.domain.usecase.photomemory.story

import com.example.travel.core.common.result.Resource
import com.example.travel.domain.model.StoryChapter
import com.example.travel.domain.model.TravelStory
import com.example.travel.domain.repository.TravelStoryRepository
import java.io.File
import javax.inject.Inject

class ExportTravelStoryUseCase @Inject constructor(
    private val travelStoryRepository: TravelStoryRepository
) {
    suspend operator fun invoke(story: TravelStory, chapters: List<StoryChapter>): Resource<File> {
        return travelStoryRepository.exportStoryAsPdf(story, chapters)
    }
}
