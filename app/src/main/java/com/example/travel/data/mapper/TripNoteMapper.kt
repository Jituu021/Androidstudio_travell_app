package com.example.travel.data.mapper

import com.example.travel.data.local.db.entity.TripNoteEntity
import com.example.travel.domain.model.TripNote

fun TripNoteEntity.toDomain(): TripNote {
    return TripNote(
        id = id,
        title = title,
        content = content,
        tag = tag,
        timestamp = timestamp
    )
}

fun TripNote.toEntity(): TripNoteEntity {
    return TripNoteEntity(
        id = id,
        title = title,
        content = content,
        tag = tag,
        timestamp = timestamp
    )
}
