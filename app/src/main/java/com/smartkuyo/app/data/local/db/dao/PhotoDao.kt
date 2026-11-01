package com.smartkuyo.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.smartkuyo.app.data.local.db.entity.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photos WHERE deceasedId = :deceasedId ORDER BY displayOrder")
    fun getPhotosForDeceased(deceasedId: Long): Flow<List<PhotoEntity>>

    @Insert
    suspend fun insertPhoto(photo: PhotoEntity): Long

    @Delete
    suspend fun deletePhoto(photo: PhotoEntity)

    @Query("UPDATE photos SET displayOrder = :order WHERE id = :photoId")
    suspend fun updateOrder(photoId: Long, order: Int)

    @Query("DELETE FROM photos")
    suspend fun deleteAll()
}
