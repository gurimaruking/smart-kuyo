package com.smartkuyo.app.data.mapper

import com.smartkuyo.app.data.local.db.entity.PhotoEntity
import com.smartkuyo.app.domain.model.Photo
import java.time.Instant

fun PhotoEntity.toDomain(): Photo = Photo(
    id = id,
    deceasedId = deceasedId,
    uri = uri,
    displayOrder = displayOrder,
    isMonochrome = isMonochrome,
    addedAt = Instant.ofEpochMilli(addedAt)
)

fun Photo.toEntity(): PhotoEntity = PhotoEntity(
    id = id,
    deceasedId = deceasedId,
    uri = uri,
    displayOrder = displayOrder,
    isMonochrome = isMonochrome,
    addedAt = addedAt.toEpochMilli()
)
