package com.example.smartkuyo.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smartkuyo.data.local.db.entity.DeceasedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeceasedDao {
    @Query("SELECT * FROM deceased ORDER BY createdAt DESC")
    fun getAllDeceased(): Flow<List<DeceasedEntity>>

    @Query("SELECT * FROM deceased WHERE id = :id")
    fun getDeceasedById(id: Long): Flow<DeceasedEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeceased(deceased: DeceasedEntity): Long

    @Update
    suspend fun updateDeceased(deceased: DeceasedEntity)

    @Delete
    suspend fun deleteDeceased(deceased: DeceasedEntity)

    @Query("DELETE FROM deceased")
    suspend fun deleteAll()
}
