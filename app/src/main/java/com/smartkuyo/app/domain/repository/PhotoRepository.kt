package com.smartkuyo.app.domain.repository

import com.smartkuyo.app.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotosForDeceased(deceasedId: Long): Flow<List<Photo>>
    suspend fun insertPhoto(photo: Photo): Long
    suspend fun deletePhoto(photo: Photo)
    suspend fun updateOrder(photoId: Long, order: Int)
}
