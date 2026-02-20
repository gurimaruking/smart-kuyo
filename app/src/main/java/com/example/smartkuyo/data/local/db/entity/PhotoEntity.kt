package com.example.smartkuyo.data.local.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [ForeignKey(
        entity = DeceasedEntity::class,
        parentColumns = ["id"],
        childColumns = ["deceasedId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["deceasedId"])]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deceasedId: Long,
    val uri: String,
    val displayOrder: Int,
    val isMonochrome: Boolean,
    val addedAt: Long
)
