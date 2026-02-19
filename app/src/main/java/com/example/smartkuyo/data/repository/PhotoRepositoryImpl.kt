package com.example.smartkuyo.data.repository

import com.example.smartkuyo.data.local.db.dao.PhotoDao
import com.example.smartkuyo.data.mapper.toDomain
import com.example.smartkuyo.data.mapper.toEntity
import com.example.smartkuyo.domain.model.Photo
import com.example.smartkuyo.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val photoDao: PhotoDao
) : PhotoRepository {

    override fun getPhotosForDeceased(deceasedId: Long): Flow<List<Photo>> =
        photoDao.getPhotosForDeceased(deceasedId).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun insertPhoto(photo: Photo): Long =
        photoDao.insertPhoto(photo.toEntity())

    override suspend fun deletePhoto(photo: Photo) =
        photoDao.deletePhoto(photo.toEntity())

    override suspend fun updateOrder(photoId: Long, order: Int) =
        photoDao.updateOrder(photoId, order)
}
