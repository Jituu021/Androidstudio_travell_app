package com.example.travel.domain.usecase.photomemory

import com.example.travel.domain.model.PhotoMemory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class GroupPhotosUseCase @Inject constructor() {
    operator fun invoke(photos: List<PhotoMemory>): Map<String, List<PhotoMemory>> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return photos.groupBy { dateFormat.format(Date(it.captureTimestamp)) }
    }
}
