package com.example.smartkuyo.domain.repository

import com.example.smartkuyo.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotosForDeceased(deceasedId: Long): Flow<List<Photo>>
    suspend fun insertPhoto(photo: Photo): Long
    suspend fun deletePhoto(photo: Photo)
    suspend fun updateOrder(photoId: Long, order: Int)
}
