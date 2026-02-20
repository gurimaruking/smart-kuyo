package com.example.smartkuyo.domain.model

import java.time.Instant

data class Photo(
    val id: Long = 0,
    val deceasedId: Long,
    val uri: String,
    val displayOrder: Int,
    val isMonochrome: Boolean = false,
    val addedAt: Instant = Instant.now()
)
