package com.smartkuyo.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deceased")
data class DeceasedEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val familyName: String,
    val givenName: String,
    val posthumousName: String?,
    val denominationCode: String,
    val dateOfDeath: Long,
    val dateOfBirth: Long?,
    val ageAtDeath: Int?,
    val createdAt: Long
)
